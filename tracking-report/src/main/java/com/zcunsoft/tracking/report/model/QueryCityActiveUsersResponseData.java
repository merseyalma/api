package com.zcunsoft.tracking.report.model;

import lombok.Data;

import java.util.List;

@Data
public class QueryCityActiveUsersResponseData {

    private long total;

    private List<CityUser> rows;
}
