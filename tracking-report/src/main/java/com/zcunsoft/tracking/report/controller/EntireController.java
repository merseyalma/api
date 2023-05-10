package com.zcunsoft.tracking.report.controller;


import com.zcunsoft.tracking.report.model.EntireStatRequest;
import com.zcunsoft.tracking.report.model.EntireStatResponse;
import com.zcunsoft.tracking.report.services.IReportService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(path = "/entire")
public class EntireController {

    @Resource
    IReportService reportService;

    @RequestMapping(path = "/getStat", method = RequestMethod.POST)
    public EntireStatResponse getStat(@RequestBody EntireStatRequest entireStatRequest) {
        return reportService.getEntireStat(entireStatRequest);
    }


}
