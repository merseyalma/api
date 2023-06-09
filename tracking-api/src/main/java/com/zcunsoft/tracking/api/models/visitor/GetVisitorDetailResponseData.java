package com.zcunsoft.tracking.api.models.visitor;

import com.zcunsoft.tracking.api.models.ResponseBase;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailResponseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "获取访客详情的响应")
@Data
public class GetVisitorDetailResponseData {

    @Schema(description = "新访客")
    private VisitorDetail newVisitor;
    @Schema(description = "老访客")
    private VisitorDetail oldVisitor;
    @Schema(description = "统计")
    private VisitorStat total;


}
