package com.zcunsoft.tracking.api.models.visituri;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "获取受访页面详情的请求")
@Data
public class GetVisitUriDetailRequest {

    @Schema(description = "时间类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "day")
    private String timeType;

    @Schema(description = "渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"android\"]")
    private List<String> channel = new ArrayList<>();

    @Schema(description = "地域", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"上海\"]")
    private List<String> area;

    @Schema(description = "访客类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "新访客")
    private String visitorType;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-08")
    private Timestamp startTime;
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-10")
    private Timestamp endTime;


}
