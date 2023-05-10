package com.zcunsoft.tracking.report.model;

import com.zcunsoft.tracking.report.entity.clickhouse.PageviewReportBydate;
import lombok.Data;

import java.util.List;

@Data
public class QueryVisitResponseData {

    private long total;

    private List<PageviewReportBydate> rows;
}
