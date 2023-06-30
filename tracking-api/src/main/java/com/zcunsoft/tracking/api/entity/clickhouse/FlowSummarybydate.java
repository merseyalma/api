package com.zcunsoft.tracking.api.entity.clickhouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.sql.Timestamp;

@Entity(name = "flow_summary_bydate")
@Data
public class FlowSummarybydate {

    @Id
    @Column
    Timestamp statDate;
    @Column
    @Id
    String lib;
    @Column
    String projectName;

    @Column
    String country;
    @Column
    String province;
    @Column
    String city;

    @Column
    Integer pv;

    @Column
    Integer visitCount;

    @Column
    Integer uv;

    @Column
    Integer ipCount;

    @Column
    Integer visitTime;

    @Column
    Integer bounceCount;


    @Column
    Timestamp updateTime;
}
