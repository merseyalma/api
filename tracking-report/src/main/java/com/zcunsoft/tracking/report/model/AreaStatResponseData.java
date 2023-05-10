package com.zcunsoft.tracking.report.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class AreaStatResponseData {


    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp startTime;


    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp area_bydate_updatetime;

    private List<String> times;


    private List<String> active_users_areas;

    private List<Long> active_users_areas_list;

    private List<List<Long>> active_users_areas_bytime_list;


    private List<String> new_users_areas;

    private List<Long> new_users_areas_list;

    private List<List<Long>> new_users_areas_bytime_list;


}
