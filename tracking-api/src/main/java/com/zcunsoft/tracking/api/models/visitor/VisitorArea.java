package com.zcunsoft.tracking.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访客地域统计")
@Data
public class VisitorArea {

    @Schema(description = "地域")
    private String area;

    @Schema(description = "访问次数")
    private int visit;

}
