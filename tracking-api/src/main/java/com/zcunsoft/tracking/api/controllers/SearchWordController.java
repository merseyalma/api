package com.zcunsoft.tracking.api.controllers;

import com.zcunsoft.tracking.api.models.searchword.GetSearchWordDetailRequest;
import com.zcunsoft.tracking.api.models.searchword.GetSearchWordDetailResponse;
import com.zcunsoft.tracking.api.models.searchword.SearchWordDetail;
import com.zcunsoft.tracking.api.models.sourcewebsite.GetSourceWebsiteDetailRequest;
import com.zcunsoft.tracking.api.models.sourcewebsite.GetSourceWebsiteDetailResponse;
import com.zcunsoft.tracking.api.models.sourcewebsite.GetSourceWebsiteDetailResponseData;
import com.zcunsoft.tracking.api.models.sourcewebsite.SourceWebsiteDetail;
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
@RequestMapping(path = "searchword")
@Tag(name = "搜索词分析", description = "搜索词分析")
public class SearchWordController {

    @Operation(summary = "获取搜索词详情")
    @RequestMapping(path = "/getSearchWordDetail", method = RequestMethod.POST)
    public GetSearchWordDetailResponse getSearchWordDetail(@RequestBody GetSearchWordDetailRequest getSearchWordDetailRequest, HttpServletRequest request) {

        GetSearchWordDetailResponse response = new GetSearchWordDetailResponse();
        List<SearchWordDetail> responseData = new ArrayList<>();
        SearchWordDetail searchWordDetail = new SearchWordDetail();
        responseData.add(searchWordDetail);
        response.setData(responseData);
        return response;
    }
}
