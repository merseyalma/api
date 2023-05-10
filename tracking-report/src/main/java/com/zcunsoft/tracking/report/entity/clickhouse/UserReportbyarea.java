package com.zcunsoft.tracking.report.entity.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.sql.Timestamp;

@Entity(name = "sensor_user_report_byarea")
@Data
@IdClass(UserReportBydateComId.class)
public class UserReportbyarea {

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
    String country;
    @Column
    String province;
    @Column
    String city;

    @Column
    Long new_users;
    @Column
    Long old_users;
    @Column
    Long active_users;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column
    Timestamp update_time;
}
