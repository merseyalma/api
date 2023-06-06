package com.zcunsoft.tracking.report.entity.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zcunsoft.tracking.report.entity.clickhouse.UserReportBydateComId;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.sql.Timestamp;

@Entity(name = "sensor_user_detail_bydate")
@Data
@IdClass(UserReportBydateComId.class)
public class UserSpecificList {
    UserReportBydateComId id;

    public UserReportBydateComId getId() {
        return id;
    }

    public void setId(UserReportBydateComId id) {
        this.id = id;
    }
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Id
    @Column
    Timestamp stat_date;
    @Column
    String download_channel;
    @Column
    String project_name;
    @Id
    @Column
    String lib;

    @Column
    String app_version;
    @Column
    String distinct_id;
    @Column
    Long pv;

    @Column
    Long duration;

    @Column
    String client_ip;
}
