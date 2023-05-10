package com.zcunsoft.tracking.report.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class LifeCycleStatResponseData {


    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp startTime;


    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp user_life_bydate_updatetime;

    private List<String> times;

    private Long new_users;
    private Long continuous_active_users;
    private Long revisit_users;
    private Long silent_users;
    private Long churn_users;
    private Float new_and_revisite_churn_percent;

    private Float new_users_raise_percent;
    private Float continuous_active_users_raise_percent;
    private Float revisit_users_raise_percent;
    private Float silent_users_raise_percent;
    private Float churn_users_raise_percent;
    private Float new_and_revisite_churn_percent_raise_percent;

    private List<Long> new_users_list;
    private List<Long> continuous_active_users_list;
    private List<Long> revisit_users_list;
    private List<Long> silent_users_list;
    private List<Long> churn_users_list;

    private List<Float> new_and_revisite_churn_percent_list;
}
