package com.zcunsoft.tracking.report.controller;


import com.zcunsoft.tracking.report.model.*;
import com.zcunsoft.tracking.report.services.IReportService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(path = "/user")
public class UserController {
    @Resource
    IReportService reportService;

    @RequestMapping(path = "/getlist", method = RequestMethod.POST)
    public QueryUserStatResponse getlist(@RequestBody QueryUserStatRequest queryUserStatRequest) {
        return reportService.getUserStatlist(queryUserStatRequest);
    }

    @RequestMapping(path = "/getLifeCycleStat", method = RequestMethod.POST)
    public LifeCycleStatResponse getLifeCycleStat(@RequestBody LifeCycleStatRequest lifeCycleStatRequest) {
        return reportService.getLifeCycleStat(lifeCycleStatRequest);
    }

    @RequestMapping(path = "/getAreaStat", method = RequestMethod.POST)
    public AreaStatResponse getAreaStat(@RequestBody AreaStatRequest areaStatRequest) {
        return reportService.getAreaStat(areaStatRequest);
    }

    @RequestMapping(path = "/getCityNewUsersList", method = RequestMethod.POST)
    public QueryCityNewUsersResponse getCityNewUsersList(@RequestBody QueryCityNewUsersRequest queryCityNewUsersRequest) {
        return reportService.getCityNewUsersList(queryCityNewUsersRequest);
    }


    @RequestMapping(path = "/getCityActiveUsersList", method = RequestMethod.POST)
    public QueryCityActiveUsersResponse getCityActiveUsersList(@RequestBody QueryCityActiveUsersRequest queryCityActiveUsersRequest) {
        return reportService.getCityActiveUsersList(queryCityActiveUsersRequest);
    }

    @RequestMapping(path = "/getUserStat", method = RequestMethod.POST)
    public QuerySpecificUserListResponse getUserStat(@RequestBody QuerySpecificUserStatRequest querySpecificUserStatRequest) {
        return reportService.getUserStat(querySpecificUserStatRequest);
    }

    @RequestMapping(path = "/getUserDetail", method = RequestMethod.POST)
    public QueryUserDetailRespone getUserDetail(@RequestBody QueryUserDetailRequest  queryUserDetailRequest) {
        return reportService.getUserDetail(queryUserDetailRequest);
    }
}
