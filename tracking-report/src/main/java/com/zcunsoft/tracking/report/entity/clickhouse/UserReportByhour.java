package com.zcunsoft.tracking.report.entity.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.sql.Timestamp;

@Entity(name = "sensor_user_report_byhour")
@Data
@IdClass(UserReportBydateComId.class)
public class UserReportByhour {

    UserReportBydateComId id;

    public UserReportBydateComId getId() {
        return id;
    }

    public void setId(UserReportBydateComId id) {
        this.id = id;
    }

    @Id
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column
    Timestamp stat_date;

    @Column
    String stat_hour;
    @Column
    @Id
    String lib;
    @Column
    Long new_users;
    @Column
    Long old_users;
    @Column
    Long active_users;

    @Column
    Long new_pv;

    @Column
    String project_name;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column
    Timestamp update_time;
}
