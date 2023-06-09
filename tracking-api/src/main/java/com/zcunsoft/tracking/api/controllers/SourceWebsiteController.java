package com.zcunsoft.tracking.api.controllers;

import com.zcunsoft.tracking.api.models.sourcewebsite.GetSourceWebsiteDetailRequest;
import com.zcunsoft.tracking.api.models.sourcewebsite.GetSourceWebsiteDetailResponse;
import com.zcunsoft.tracking.api.models.sourcewebsite.GetSourceWebsiteDetailResponseData;
import com.zcunsoft.tracking.api.models.sourcewebsite.SourceWebsiteDetail;
import com.zcunsoft.tracking.api.models.summary.GetSourceWebsiteResponseData;
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
@RequestMapping(path = "sourcewebsite")
@Tag(name = "来源网站分析", description = "来源网站分析")
public class SourceWebsiteController {

    @Operation(summary = "获取来源网站详情")
    @RequestMapping(path = "/getSourceSiteDetail", method = RequestMethod.POST)
    public GetSourceWebsiteDetailResponse getSourceSiteDetail(@RequestBody GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest, HttpServletRequest request) {

        GetSourceWebsiteDetailResponse response = new GetSourceWebsiteDetailResponse();
        GetSourceWebsiteDetailResponseData getSourceWebsiteDetailResponseData =new GetSourceWebsiteDetailResponseData();
        getSourceWebsiteDetailResponseData.setTotal(new SourceWebsiteDetail());
        List<SourceWebsiteDetail> responseData= new ArrayList<>();
        SourceWebsiteDetail sourceWebsiteDetail =new SourceWebsiteDetail();
        responseData.add(sourceWebsiteDetail);
        getSourceWebsiteDetailResponseData.setDetail(responseData);
        response.setData(getSourceWebsiteDetailResponseData);
        return response;
    }
}
