package com.zcunsoft.tracking.api.models.trend;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zcunsoft.tracking.api.models.summary.FlowSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "流量信息")
@Data
public class FlowDetail   {

    @Schema(description = "统计日期")
    private String statDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "统计小时")
    private String statHour;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "访问次数")
    private int visitCount;

    @Schema(description = "新访客数")
    private int newUv;

    @Schema(description = "访客数(UV)")
    private int uv;

    @Schema(description = "IP数")
    private int ipCount;

    @Schema(description = "平均访问页数")
    private float avgPv;

    @Schema(description = "平均访问时长")
    private float avgVisitTime;

    @Schema(description = "跳出率")
    private float bounceRate;

    @Schema(description = "渠道")
    private String channel;
}
