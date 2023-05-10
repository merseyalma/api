package com.zcunsoft.tracking.report.entity.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.sql.Timestamp;

@Entity(name = "sensor_pageview_report_bydate")
@Data
@IdClass(UserReportBydateComId.class)
public class PageviewReportBydate {

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
    Long impression;

    @Column
    Long impression_uv;

    @Column
    Long clicks;

    @Column
    Long clicks_uv;

    @Column
    String project_name;
}
