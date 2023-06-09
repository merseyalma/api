package com.zcunsoft.tracking.api.controllers;

import com.zcunsoft.tracking.api.models.summary.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "summary")
@Tag(name = "概览", description = "概览")
public class SummaryController {

    @Operation(summary = "获取流量概览")
    @RequestMapping(path = "/getFlow", method = RequestMethod.POST)
    public GetFlowResponse getFlow(@RequestBody GetFlowRequest getFlowRequest, HttpServletRequest request) {

        GetFlowResponse response = new GetFlowResponse();
        GetFlowResponseData responseData=new GetFlowResponseData();
        FlowSummary summary = new FlowSummary();
        responseData.setCurrent(summary);
        responseData.setPrevious(summary);
        responseData.setSamePeriod(summary);

        response.setData(responseData);
        return response;
    }

    @Operation(summary = "获取流量趋势")
    @RequestMapping(path = "/getFlowTrend", method = RequestMethod.POST)
    public GetFlowTrendResponse getFlowTrend(@RequestBody GetFlowTrendRequest getFlowTrendRequest, HttpServletRequest request) {

        GetFlowTrendResponse response = new GetFlowTrendResponse();
        GetFlowTrendResponseData responseData=new GetFlowTrendResponseData();
        response.setData(responseData);
        return response;
    }

    @Operation(summary = "获取Top10搜索词")
    @RequestMapping(path = "/getSearchWord", method = RequestMethod.POST)
    public GetSearchWordResponse getSearchWord(@RequestBody GetSearchWordRequest getSearchWordRequest, HttpServletRequest request) {

        GetSearchWordResponse response = new GetSearchWordResponse();
        List<GetSearchWordResponseData> responseDataList = new ArrayList<>();
        GetSearchWordResponseData responseData=new GetSearchWordResponseData();
        responseDataList.add(responseData);
        response.setData(responseDataList);
        return response;
    }


    @Operation(summary = "获取Top10来源网站")
    @RequestMapping(path = "/getSourceWebsite", method = RequestMethod.POST)
    public GetSourceWebsiteResponse getSourceWebsite(@RequestBody GetSourceWebsiteRequest getSourceWebsiteRequest, HttpServletRequest request) {

        GetSourceWebsiteResponse response = new GetSourceWebsiteResponse();
        List<GetSourceWebsiteResponseData> responseDataList = new ArrayList<>();
        GetSourceWebsiteResponseData responseData=new GetSourceWebsiteResponseData();
        responseDataList.add(responseData);
        response.setData(responseDataList);
        return response;
    }

    @Operation(summary = "获取Top10受访页面")
    @RequestMapping(path = "/getVisitUri", method = RequestMethod.POST)
    public GetVisitUriResponse getVisitUri(@RequestBody GetVisitUriRequest getVisitUriRequest, HttpServletRequest request) {

        GetVisitUriResponse response = new GetVisitUriResponse();
        List<GetVisitUriResponseData> responseDataList = new ArrayList<>();
        GetVisitUriResponseData responseData=new GetVisitUriResponseData();
        responseDataList.add(responseData);
        response.setData(responseDataList);
        return response;
    }

    @Operation(summary = "获取访客")
    @RequestMapping(path = "/getVisitor", method = RequestMethod.POST)
    public GetVisitorResponse getVisitor(@RequestBody GetVisitorRequest getVisitorRequest, HttpServletRequest request) {

        GetVisitorResponse response = new GetVisitorResponse();
        GetVisitorResponseData responseData=new GetVisitorResponseData();
        responseData.setNewVisitor(new FlowSummary());
        responseData.setOldVisitor(new FlowSummary());
        response.setData(responseData);
        return response;
    }

    @Operation(summary = "获取地域")
    @RequestMapping(path = "/getArea", method = RequestMethod.POST)
    public GetAreaResponse getArea(@RequestBody GetVisitorRequest getAreaRequest, HttpServletRequest request) {

        GetAreaResponse response = new GetAreaResponse();
        List<GetAreaResponseData> responseDataList = new ArrayList<>();
        GetAreaResponseData responseData=new GetAreaResponseData();
        responseDataList.add(responseData);
        response.setData(responseDataList);
        return response;
    }


}
