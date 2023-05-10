package com.zcunsoft.tracking.report.model;

import com.zcunsoft.tracking.report.entity.clickhouse.UserReportBydate;
import lombok.Data;

import java.util.List;

@Data
public class QueryUserStatResponseData {

    private long total;

    private List<UserReportBydate> rows;
}
