package com.zcunsoft.tracking.api.services;

import com.zcunsoft.tracking.api.models.summary.GetFlowRequest;
import com.zcunsoft.tracking.api.models.summary.GetFlowResponse;

public interface IReportService {

    GetFlowResponse getFlow(GetFlowRequest getFlowRequest);
}
