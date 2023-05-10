package com.zcunsoft.tracking.report.cfg;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The Class SpringConfiguration.
 *
 * @author yuhao
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties({ ServiceSetting.class })
public class SpringConfiguration {


}
