package com.zcunsoft.tracking.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Schema(description = "获取流量趋势的请求")
@Data
public class GetFlowTrendRequest {

    @Schema(description = "时间类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "day")
    private String timeType;

    @Schema(description = "渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "android")
    private String channel;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-08")
    private Timestamp startTime;
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-10")
    private Timestamp endTime;
}
