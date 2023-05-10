package com.zcunsoft.tracking.report.entity.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.sql.Timestamp;

@Entity(name = "sensor_user_retention_bydate")
@Data
@IdClass(UserReportBydateComId.class)
public class UserRetentionBydate {

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
    Long d1;
    @Column
    Long d3;
    @Column
    Long d7;
    @Column
    Long d14;
    @Column
    Long d30;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column
    Timestamp update_time;
}
