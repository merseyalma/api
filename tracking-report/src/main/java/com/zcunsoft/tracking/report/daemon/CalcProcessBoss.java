package com.zcunsoft.tracking.report.daemon;

import com.zcunsoft.tracking.report.cfg.ServiceSetting;
import com.zcunsoft.tracking.report.services.IStatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Calendar;

@Component
public class CalcProcessBoss {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    Thread thread = null;

    @Resource
    private IStatService statService;

    @Resource
    private ServiceSetting setting;

    boolean running = false;

    private static final int LOOP_SPAN = 300000;

    @PostConstruct
    public void start() throws Exception {
        running = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                work();
            }
        }, "CalcProcessBoss");

        thread.start();
    }

    private void work() {
        running = true;
        Calendar loopShoudEndTime = Calendar.getInstance();

        while (running) {
            try {
                statService.statAppVersion();
                statService.statDownloadChannel();
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("work failed", ex);
                }
            }

            Calendar loopEndTime = Calendar.getInstance();
            if (loopEndTime.compareTo(loopShoudEndTime) <= 0) {
                long sleepTime = loopShoudEndTime.getTimeInMillis() - loopEndTime.getTimeInMillis() + 1;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    running = false;
                    break;
                }
                loopShoudEndTime.add(Calendar.MILLISECOND, LOOP_SPAN);
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error("Last round work finished after expected time point, cost more {}ms.",
                            loopEndTime.getTimeInMillis() - loopShoudEndTime.getTimeInMillis());
                }
                loopShoudEndTime.add(Calendar.MILLISECOND,
                        (int) (LOOP_SPAN + (loopEndTime.getTimeInMillis() - loopShoudEndTime.getTimeInMillis())));
            }
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (logger.isInfoEnabled()) {
            logger.info(thread.getName() + " stopping...");
        }
    }
}