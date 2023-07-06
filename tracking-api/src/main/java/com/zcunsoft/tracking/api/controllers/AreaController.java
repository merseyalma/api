package com.zcunsoft.tracking.api.controllers;

import com.zcunsoft.tracking.api.models.area.*;
import com.zcunsoft.tracking.api.models.summary.GetAreaResponse;
import com.zcunsoft.tracking.api.models.summary.GetAreaResponseData;
import com.zcunsoft.tracking.api.models.summary.GetVisitorSummaryRequest;
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
@RequestMapping(path = "area")
@Tag(name = "地域分析", description = "地域分析")
public class AreaController {

    @Resource
    IReportService reportService;

    @Operation(summary = "获取地域")
    @RequestMapping(path = "/getArea", method = RequestMethod.POST)
    public GetAreaResponse getArea(@RequestBody GetVisitorSummaryRequest getAreaRequest, HttpServletRequest request) {
        return reportService.getArea(getAreaRequest);
    }

    @Operation(summary = "获取地域详情")
    @RequestMapping(path = "/getAreaDetail", method = RequestMethod.POST)
    public GetAreaDetailResponse getAreaDetail(@RequestBody GetAreaDetailRequest getAreaDetailRequest, HttpServletRequest request) {

        GetAreaDetailResponse response = new GetAreaDetailResponse();
        GetAreaDetailResponseData responseData = new GetAreaDetailResponseData();
        responseData.setTotal(new AreaDetail());
        List<AreaDetail> areaDetailList = new ArrayList<>();
        AreaDetail areaDetail = new AreaDetail();
        areaDetailList.add(areaDetail);
        responseData.setDetail(areaDetailList);
        response.setData(responseData);
        return response;
    }
}
