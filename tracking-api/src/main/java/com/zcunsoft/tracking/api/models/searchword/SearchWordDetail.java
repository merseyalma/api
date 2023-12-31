package com.zcunsoft.tracking.api.models.searchword;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "搜索词信息")
@Data
public class SearchWordDetail {
    @Schema(description = "统计日期")
    private String statDate;

    @Schema(description = "搜索词")
    private String word;

    @Schema(description = "浏览量(PV)")
    private int pv;
    @Schema(description = "浏览量占比")
    private float pvRate;

    @Schema(description = "平均访问页数")
    private float avgPv;

    @Schema(description = "平均访问时长")
    private int avgVisitTime;

    @Schema(description = "跳出率")
    private float bounceRate;
}
