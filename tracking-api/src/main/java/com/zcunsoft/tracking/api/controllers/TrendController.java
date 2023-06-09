package com.zcunsoft.tracking.api.controllers;

import com.zcunsoft.tracking.api.models.summary.GetFlowTrendRequest;
import com.zcunsoft.tracking.api.models.summary.GetFlowTrendResponseData;
import com.zcunsoft.tracking.api.models.trend.FlowDetail;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailRequest;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailResponse;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailResponseData;
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
@RequestMapping(path = "trend")
@Tag(name = "趋势分析", description = "趋势分析")
public class TrendController {

    @Operation(summary = "获取流量趋势详情")
    @RequestMapping(path = "/getFlowTrendDetail", method = RequestMethod.POST)
    public GetFlowTrendDetailResponse getFlowTrend(@RequestBody GetFlowTrendDetailRequest getFlowTrendDetailRequest, HttpServletRequest request) {

        GetFlowTrendDetailResponse response = new GetFlowTrendDetailResponse();
        GetFlowTrendDetailResponseData getFlowTrendDetailResponseData =new GetFlowTrendDetailResponseData();
        getFlowTrendDetailResponseData.setTotal(new FlowDetail());
        List<FlowDetail> responseData= new ArrayList<>();
        FlowDetail flowDetail =new FlowDetail();
        responseData.add(flowDetail);
        getFlowTrendDetailResponseData.setDetail(responseData);
        response.setData(getFlowTrendDetailResponseData);
        return response;
    }
}
