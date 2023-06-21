package com.zcunsoft.tracking.api.controllers;

import com.zcunsoft.tracking.api.models.summary.GetVisitUriRequest;
import com.zcunsoft.tracking.api.models.summary.GetVisitUriResponse;
import com.zcunsoft.tracking.api.models.summary.GetVisitUriResponseData;
import com.zcunsoft.tracking.api.models.visituri.*;
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
@RequestMapping(path = "visituri")
@Tag(name = "受访页面分析", description = "受访页面分析")
public class VisitUriController {


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

    @Operation(summary = "获取地域详情")
    @RequestMapping(path = "/getVisitUriDetail", method = RequestMethod.POST)
    public GetVisitUriDetailResponse getVisitUriDetail(@RequestBody GetVisitUriDetailRequest getVisitUriDetailRequest, HttpServletRequest request) {

        GetVisitUriDetailResponse response = new GetVisitUriDetailResponse();
        GetVisitUriDetailResponseData responseData = new GetVisitUriDetailResponseData();
        responseData.setTotal(new VisitUriStat());
        List<VisitUriDetail> VisitUriDetailList = new ArrayList<>();
        VisitUriDetail VisitUriDetail = new VisitUriDetail();
        VisitUriDetailList.add(VisitUriDetail);
        responseData.setDetail(VisitUriDetailList);
        response.setData(responseData);
        return response;
    }
}
