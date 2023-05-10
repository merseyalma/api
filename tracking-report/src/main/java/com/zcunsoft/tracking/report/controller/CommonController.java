package com.zcunsoft.tracking.report.controller;


import com.zcunsoft.tracking.report.model.*;
import com.zcunsoft.tracking.report.services.IReportService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(path = "/common")
public class CommonController {
    @Resource
    IReportService reportService;


    @RequestMapping(path = "/getAppVersion", method = RequestMethod.POST)
    public GetAppVersionResponse getAppVersion() {
        return reportService.getAppVersion();
    }

    @RequestMapping(path = "/getDownloadChannel", method = RequestMethod.POST)
    public GetDownloadChannelResponse getDownloadChannel() {
        return reportService.getDownloadChannel();
    }

    @RequestMapping(path = "/getApp", method = RequestMethod.POST)
    public GetAppResponse getApp(@RequestBody GetAppRequest getAppRequest) {
        return reportService.getApp(getAppRequest);
    }
}
