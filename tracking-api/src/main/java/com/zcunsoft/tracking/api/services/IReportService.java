package com.zcunsoft.tracking.api.services;

import com.zcunsoft.tracking.api.models.summary.*;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailRequest;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailResponse;

public interface IReportService {

    GetFlowResponse getFlow(GetFlowRequest getFlowRequest);

    GetFlowTrendResponse getFlowTrend(GetFlowTrendRequest getFlowTrendRequest);

    GetVisitorSummaryResponse getVisitor(GetVisitorSummaryRequest getVisitorRequest);

    GetVisitUriResponse getVisitUri(GetVisitUriRequest getVisitUriRequest);

    GetSearchWordResponse getSearchWord(GetSearchWordRequest getSearchWordRequest);

    GetSourceWebsiteResponse getSourceWebsite(GetSourceWebsiteRequest getSourceWebsiteRequest);

    GetAreaResponse getArea(GetVisitorSummaryRequest getAreaRequest);

    GetFlowTrendDetailResponse getFlowTrendDetail(GetFlowTrendDetailRequest getFlowTrendDetailRequest);
}
