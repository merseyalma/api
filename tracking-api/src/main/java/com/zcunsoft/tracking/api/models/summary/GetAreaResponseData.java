package com.zcunsoft.tracking.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取地域的响应")
@Data
public class GetAreaResponseData {

    @Schema(description = "省份")
    private String province = "";

    @Schema(description = "浏览量(PV)")
    private int pv;
}
