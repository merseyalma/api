package com.zcunsoft.tracking.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "获取流量趋势的响应")
@Data
public class GetFlowTrendResponseData {

    @Schema(description = "时间")
    private List<String> times = new ArrayList<>();

    @Schema(description = "浏览量(PV)")
    private List<Integer> pv = new ArrayList<>();

    @Schema(description = "访问次数")
    private List<Integer> visit = new ArrayList<>();

    @Schema(description = "访客数(UV)")
    private List<Integer> uv = new ArrayList<>();

    @Schema(description = "IP数")
    private List<Integer> ipCount = new ArrayList<>();

    @Schema(description = "平均访问页数")
    private List<Float> avgPv = new ArrayList<>();

    @Schema(description = "平均访问时长")
    private List<Integer> avgVisitTime = new ArrayList<>();

    @Schema(description = "跳出率")
    private List<Float> bounceRate = new ArrayList<>();


}
