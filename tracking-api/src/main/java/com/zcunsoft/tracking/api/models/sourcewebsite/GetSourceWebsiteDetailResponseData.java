package com.zcunsoft.tracking.api.models.sourcewebsite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "获取来源网站详情的响应")
@Data
public class GetSourceWebsiteDetailResponseData {

    private SourceWebsiteDetail total;

    private List<SourceWebsiteDetail> detail;
}