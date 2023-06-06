package com.zcunsoft.tracking.report.model;


import com.zcunsoft.tracking.report.entity.clickhouse.UserSpecificList;
import lombok.Data;

import java.util.List;
@Data
public class QuerySpecificUserListResponeData {
    private long total;

    private List<UserSpecificList> rows;
}
