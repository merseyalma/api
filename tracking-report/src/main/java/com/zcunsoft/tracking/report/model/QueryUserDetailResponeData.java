package com.zcunsoft.tracking.report.model;


import lombok.Data;
import org.apache.catalina.User;

import java.util.List;

@Data
public class QueryUserDetailResponeData {
    private long total;

    private List<UserDetailData> rows;
}
