package com.zcunsoft.tracking.report.controller;


import com.zcunsoft.tracking.report.model.RealStatRequest;
import com.zcunsoft.tracking.report.model.RealStatResponse;
import com.zcunsoft.tracking.report.services.IReportService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(path = "/real")
public class RealController {

    @Resource
    IReportService reportService;

    @RequestMapping(path = "/getStat", method = RequestMethod.POST)
    public RealStatResponse getStat(@RequestBody RealStatRequest realStatRequest) {
        return reportService.getRealStat(realStatRequest);
    }
}
