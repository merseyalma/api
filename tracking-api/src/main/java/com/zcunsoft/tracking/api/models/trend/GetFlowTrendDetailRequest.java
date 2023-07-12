package com.zcunsoft.tracking.api.models.trend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "获取流量趋势详情的请求")
@Data
public class GetFlowTrendDetailRequest {

    @Schema(description = "时间类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "day,hour")
    private String timeType;

    @Schema(description = "渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"安卓\",\"苹果\",\"网站\",\"微信小程序\"]")
    private List<String> channel = new ArrayList<>();

    @Schema(description = "地域", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"上海\",\"北京\"]")
    private List<String> area;

    @Schema(description = "访客类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "新访客,老访客")
    private String visitorType;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-08")
    private String startTime;
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-10")
    private String endTime;

    @Schema(description = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    private String projectName;
}
