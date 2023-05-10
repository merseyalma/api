package com.zcunsoft.tracking.report.entity.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.sql.Timestamp;

@Entity(name = "sensor_user_retention_byweek")
@Data
@IdClass(UserReportBydateComId.class)
public class UserRetentionByweek {

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Id
    @Column
    Timestamp stat_date;
    @Column
    @Id
    String lib;
    @Column
    String project_name;
    @Column
    Long new_users;
    @Column
    Long w1;
    @Column
    Long w8;
    @Column
    Long w12;
    @Column
    Long w24;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column
    Timestamp update_time;
}
