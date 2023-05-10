package com.zcunsoft.tracking.report.entity.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.sql.Timestamp;

@Entity(name = "sensor_user_life_report_bydate")
@Data
@IdClass(UserReportBydateComId.class)
public class UserLifeReportBydate {

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
    @Id
    String lib;

    @Column
    String project_name;

    @Column
    Long new_users;
    @Column
    Long continuous_active_users;
    @Column
    Long revisit_users;

    @Column
    Long silent_users;

    @Column
    Long churn_users;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column
    Timestamp update_time;

}
