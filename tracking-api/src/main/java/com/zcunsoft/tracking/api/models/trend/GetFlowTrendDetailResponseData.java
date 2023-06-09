package com.zcunsoft.tracking.api.models.trend;

import com.zcunsoft.tracking.api.models.ResponseBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "获取流量趋势详情的响应")
@Data
public class GetFlowTrendDetailResponseData  {

    private FlowDetail total;

    private List<FlowDetail> detail;
}
