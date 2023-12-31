package com.zcunsoft.tracking.api.models.visituri;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "受访页面详情")
@Data
public class VisitUriDetail {
    @Schema(description = "页面")
    private String uri;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "访客数(UV)")
    private int uv;

    @Schema(description = "IP数")
    private int ipCount;

    @Schema(description = "退出页次数")
    private int exitCount;

    @Schema(description = "平均停留时长")
    private int avgDuration;

    @Schema(description = "退出率")
    private float exitRate;

    @Schema(description = "入口页次数")
    private float entryRate;
}
