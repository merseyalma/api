package com.zcunsoft.tracking.report.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class RealStatResponseData {

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp today;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp real_updatetime;

    private Long new_users;

    private Long old_users;

    private Long active_users;

    private Long new_pv;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp real_byhour_updatetime;

    private List<String> times;

    private List<Long> new_users_byhour;

    private Long new_users_byhour_sum;

    private Float new_users_byhour_avg;

    private List<Long> old_users_byhour;

    private Long old_users_byhour_sum;

    private Float old_users_byhour_avg;

    private List<Long> active_users_byhour;

    private Long active_users_byhour_sum;

    private Float active_users_byhour_avg;

    private List<Long> new_pv_byhour;

    private Long new_pv_byhour_sum;

    private Float new_pv_byhour_avg;

    private String latestHour;

    private Long new_users_latest;

    private Long old_users_latest;

    private Long active_users_latest;

    private Long new_pv_latest;
 }
