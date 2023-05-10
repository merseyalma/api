package com.zcunsoft.tracking.report.model;

import lombok.Data;

import java.util.List;

@Data
public class QueryCityNewUsersResponseData {

    private long total;

    private List<CityUser> rows;
}
