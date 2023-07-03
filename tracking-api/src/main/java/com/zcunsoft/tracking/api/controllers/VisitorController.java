package com.zcunsoft.tracking.api.controllers;

import com.zcunsoft.tracking.api.models.summary.FlowSummary;
import com.zcunsoft.tracking.api.models.summary.GetVisitorSummaryRequest;
import com.zcunsoft.tracking.api.models.summary.GetVisitorSummaryResponse;
import com.zcunsoft.tracking.api.models.summary.GetVisitorSummaryResponseData;
import com.zcunsoft.tracking.api.models.visitor.*;
import com.zcunsoft.tracking.api.models.visituri.GetVisitUriListRequest;
import com.zcunsoft.tracking.api.models.visituri.GetVisitUriListResponse;
import com.zcunsoft.tracking.api.models.visituri.GetVisitUriListResponseData;
import com.zcunsoft.tracking.api.models.visituri.VisitUri;
import com.zcunsoft.tracking.api.services.IReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "visitor")
@Tag(name = "访客分析", description = "访客分析")
public class VisitorController {
    @Resource
    IReportService reportService;


    @Operation(summary = "获取访客")
    @RequestMapping(path = "/getVisitor", method = RequestMethod.POST)
    public GetVisitorSummaryResponse getVisitor(@RequestBody GetVisitorSummaryRequest getVisitorRequest, HttpServletRequest request) {
         return reportService.getVisitor(getVisitorRequest);
    }

    @Operation(summary = "获取访客详情")
    @RequestMapping(path = "/getVisitorDetail", method = RequestMethod.POST)
    public GetVisitorDetailResponse getVisitorDetail(@RequestBody GetVisitorDetailRequest getVisitorDetailRequest, HttpServletRequest request) {

        GetVisitorDetailResponse response = new GetVisitorDetailResponse();
        GetVisitorDetailResponseData responseData = new GetVisitorDetailResponseData();
        responseData.setOldVisitor(new VisitorDetail());
        responseData.setNewVisitor(new VisitorDetail());
        responseData.setTotal(new VisitorStat());
        response.setData(responseData);
        return response;
    }

    @Operation(summary = "获取访客详情列表")
    @RequestMapping(path = "/getVisitorList", method = RequestMethod.POST)
    public GetVisitorResponse getVisitorList(@RequestBody GetVisitorRequest getVisitorRequest, HttpServletRequest request) {

        GetVisitorResponse response = new GetVisitorResponse();
        GetVisitorResponseData responseData = new GetVisitorResponseData();
        List<VisitorSummary> visitorList = new ArrayList<>();
        visitorList.add(new VisitorSummary());
        responseData.setTotal(10);
        responseData.setRows(visitorList);
        response.setData(responseData);
        return response;
    }

    @Operation(summary = "获取访客画像")
    @RequestMapping(path = "/getVisitorProfile", method = RequestMethod.POST)
    public GetVisitorProfileResponse getVisitorProfile(@RequestBody GetVisitorProfileRequest getVisitorProfileRequest, HttpServletRequest request) {

        GetVisitorProfileResponse response = new GetVisitorProfileResponse();
        VisitorProfile responseData = new VisitorProfile();
        responseData.setBaseInfo(new VisitorProfileBasicInfo());
        responseData.setSummary(new VisitorProfileSummary());

        List<VisitorDevice> deviceList = new ArrayList<>();
        deviceList.add(new VisitorDevice());
        responseData.setDeviceList(deviceList);
        List<VisitorArea> areaList = new ArrayList<>();
        areaList.add(new VisitorArea());
        responseData.setAreaList(areaList);
        response.setData(responseData);
        return response;
    }

    @Operation(summary = "获取访客的访问列表")
    @RequestMapping(path = "/getVisitList", method = RequestMethod.POST)
    public GetVisitListResponse getVisitList(@RequestBody GetVisitListRequest getVisitListRequest, HttpServletRequest request) {

        GetVisitListResponse response = new GetVisitListResponse();
        GetVisitListResponseData responseData = new GetVisitListResponseData();
        responseData.setTotal(100);

        List<Visit> visitList = new ArrayList<>();
        visitList.add(new Visit());
        responseData.setRows(visitList);
        response.setData(responseData);
        return response;
    }

    @Operation(summary = "获取访客访问的页面列表")
    @RequestMapping(path = "/getVisitUriList", method = RequestMethod.POST)
    public GetVisitUriListResponse getVisitUriList(@RequestBody GetVisitUriListRequest getVisitUriListRequest, HttpServletRequest request) {

        GetVisitUriListResponse response = new GetVisitUriListResponse();
        GetVisitUriListResponseData responseData = new GetVisitUriListResponseData();
        responseData.setTotal(100);

        List<VisitUri> visitList = new ArrayList<>();
        visitList.add(new VisitUri());
        responseData.setRows(visitList);
        response.setData(responseData);
        return response;
    }
}
