package com.zcunsoft.tracking.report.controller;


import com.zcunsoft.tracking.report.model.QueryVisitRequest;
import com.zcunsoft.tracking.report.model.QueryVisitResponse;
import com.zcunsoft.tracking.report.services.IReportService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(path = "/visit")
public class VisitController {

    @Resource
    IReportService reportService;

    @RequestMapping(path = "/getlist", method = RequestMethod.POST)
    public QueryVisitResponse getlist(@RequestBody QueryVisitRequest queryVisitRequest) {
        return reportService.getVisitlist(queryVisitRequest);
    }
}
