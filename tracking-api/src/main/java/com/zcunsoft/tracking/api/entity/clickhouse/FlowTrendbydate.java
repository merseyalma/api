package com.zcunsoft.tracking.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "flow_trend_bydate")
@Data
public class FlowTrendbydate {

    @Id
    @Column
    Timestamp statDate;

    @Id
    @Column
    String lib;

    @Id
    @Column
    String projectName;

    @Column
    Integer pv;

    @Column
    Integer visitCount;

    @Column
    Integer uv;

    @Column
    Integer newUv;

    @Column
    Integer ipCount;

    @Column
    Integer visitTime;

    @Column
    Integer bounceCount;


    @Column
    Timestamp updateTime;

    @Column
    String country;

    @Column
    String province;

    @Column
    String isFirstDay;
}
