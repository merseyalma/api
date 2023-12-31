package com.zcunsoft.tracking.api.controllers;

import com.zcunsoft.tracking.api.models.summary.*;
import com.zcunsoft.tracking.api.models.trend.FlowDetail;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailRequest;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailResponse;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailResponseData;
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
@RequestMapping(path = "flow")
@Tag(name = "趋势分析", description = "趋势分析")
public class FlowController {

    @Resource
    IReportService reportService;

    @Operation(summary = "获取流量概览")
    @RequestMapping(path = "/getFlow", method = RequestMethod.POST)
    public GetFlowResponse getFlow(@RequestBody GetFlowRequest getFlowRequest, HttpServletRequest request) {
        return reportService.getFlow(getFlowRequest);
    }

    @Operation(summary = "获取流量趋势")
    @RequestMapping(path = "/getFlowTrend", method = RequestMethod.POST)
    public GetFlowTrendResponse getFlowTrend(@RequestBody GetFlowTrendRequest getFlowTrendRequest, HttpServletRequest request) {
        return reportService.getFlowTrend(getFlowTrendRequest);
    }


    @Operation(summary = "获取流量趋势详情")
    @RequestMapping(path = "/getFlowTrendDetail", method = RequestMethod.POST)
    public GetFlowTrendDetailResponse getFlowTrendDetail(@RequestBody GetFlowTrendDetailRequest getFlowTrendDetailRequest, HttpServletRequest request) {
        return reportService.getFlowTrendDetail(getFlowTrendDetailRequest);
    }
}
