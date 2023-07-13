package com.zcunsoft.tracking.api.services;

import com.zcunsoft.tracking.api.entity.clickhouse.*;
import com.zcunsoft.tracking.api.models.enums.LibType;
import com.zcunsoft.tracking.api.models.summary.*;
import com.zcunsoft.tracking.api.models.trend.FlowDetail;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailRequest;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailResponse;
import com.zcunsoft.tracking.api.models.trend.GetFlowTrendDetailResponseData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReportServiceImpl implements IReportService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final NamedParameterJdbcTemplate clickHouseJdbcTemplate;


    private final ThreadLocal<DateFormat> yMdFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final ThreadLocal<DateFormat> yMdHmsFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public ReportServiceImpl(NamedParameterJdbcTemplate clickHouseJdbcTemplate) {
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
    }

    @Override
    public GetFlowResponse getFlow(GetFlowRequest getFlowRequest) {
        GetFlowResponse response = new GetFlowResponse();
        try {
            GetFlowResponseData responseData = null;
            if ("day".equalsIgnoreCase(getFlowRequest.getTimeType())) {
                responseData = getFlowBydate(getFlowRequest);
            }

            response.setData(responseData);
        } catch (Exception ex) {
            logger.error("getUserStatlist error," + ex.getMessage());
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public GetFlowTrendResponse getFlowTrend(GetFlowTrendRequest getFlowTrendRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from flow_summary_byhour";
        String where = "";

        List<String> channelList = new ArrayList<>();
        if (getFlowTrendRequest.getChannel() != null && !getFlowTrendRequest.getChannel().isEmpty()) {
            for (String channel : getFlowTrendRequest.getChannel()) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
        }
        if (channelList.isEmpty()) {
            channelList.add("all");
        }
        where += " and lib in (:channel)";
        paramMap.addValue("channel", channelList);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String startTime = this.yMdFORMAT.get().format(System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY);
        if (StringUtils.isNotBlank(getFlowTrendRequest.getStartTime())) {
            startTime = getFlowTrendRequest.getStartTime();
        }
        where += " and stat_date>=:starttime";
        paramMap.addValue("starttime", startTime);

        String endTime = this.yMdFORMAT.get().format(now);
        if (StringUtils.isNotBlank(getFlowTrendRequest.getEndTime())) {
            endTime = getFlowTrendRequest.getEndTime();
        }
        where += " and stat_date<=:endtime";
        paramMap.addValue("endtime", endTime);

        String projectName = getFlowTrendRequest.getProjectName();
        if (StringUtils.isBlank(projectName)) {
            projectName = "gpapp";
        }
        where += " and project_name=:project";
        paramMap.addValue("project", projectName);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }

        List<FlowSummarybyhour> flowTrendList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<FlowSummarybyhour>(FlowSummarybyhour.class));

        GetFlowTrendResponse responseData = new GetFlowTrendResponse();
        List<GetFlowTrendResponseData> flowTrendResponseDataList = new ArrayList<>();
        for (FlowSummarybyhour flowSummarybyhour : flowTrendList) {
            GetFlowTrendResponseData flowSummary = new GetFlowTrendResponseData();
            flowSummary.setTime(yMdFORMAT.get().format(flowSummarybyhour.getStatDate()) + " " + flowSummarybyhour.getStatHour() + ":00:00");
            flowSummary.setPv(flowSummarybyhour.getPv());
            flowSummary.setIpCount(flowSummarybyhour.getIpCount());
            flowSummary.setVisit(flowSummarybyhour.getVisitCount());
            flowSummary.setUv(flowSummarybyhour.getUv());
            flowSummary.setAvgPv(0);
            flowSummary.setAvgVisitTime(0);
            flowSummary.setBounceRate(0);
            if (flowSummarybyhour.getVisitCount() > 0) {
                DecimalFormat decimalFormat = new DecimalFormat("0.##");

                float avgPv = flowSummarybyhour.getPv() * 1.0f / flowSummarybyhour.getVisitCount();
                flowSummary.setAvgPv(Float.parseFloat(decimalFormat.format(avgPv)));

                float avgVisitTime = flowSummarybyhour.getVisitTime() * 1.0f / flowSummarybyhour.getVisitCount();
                flowSummary.setAvgVisitTime(Float.parseFloat(decimalFormat.format(avgVisitTime)));

                float bounceRate = flowSummarybyhour.getBounceCount() * 1.0f / flowSummarybyhour.getVisitCount();
                flowSummary.setBounceRate(Float.parseFloat(decimalFormat.format(bounceRate)));
            }
            flowSummary.setChannel(flowSummarybyhour.getLib());
            flowTrendResponseDataList.add(flowSummary);
        }
        responseData.setData(flowTrendResponseDataList);
        return responseData;
    }

    @Override
    public GetVisitorSummaryResponse getVisitor(GetVisitorSummaryRequest getVisitorRequest) {
        GetVisitorSummaryResponse response = new GetVisitorSummaryResponse();

        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from visitor_summary_bydate";
        String where = "";

        List<String> channelList = new ArrayList<>();
        if (getVisitorRequest.getChannel() != null && !getVisitorRequest.getChannel().isEmpty()) {
            for (String channel : getVisitorRequest.getChannel()) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
        }
        if (channelList.isEmpty()) {
            channelList.add("all");
        }
        where += " and lib in (:channel)";
        paramMap.addValue("channel", channelList);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String startTime = this.yMdFORMAT.get().format(System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY);
        if (StringUtils.isNotBlank(getVisitorRequest.getStartTime())) {
            startTime = getVisitorRequest.getStartTime();
        }
        where += " and stat_date>=:starttime";
        paramMap.addValue("starttime", startTime);

        String endTime = this.yMdFORMAT.get().format(now);
        if (StringUtils.isNotBlank(getVisitorRequest.getEndTime())) {
            endTime = getVisitorRequest.getEndTime();
        }
        where += " and stat_date<=:endtime";
        paramMap.addValue("endtime", endTime);

        String projectName = getVisitorRequest.getProjectName();
        if (StringUtils.isBlank(projectName)) {
            projectName = "gpapp";
        }
        where += " and project_name=:project";
        paramMap.addValue("project", projectName);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }

        List<VisitorSummarybydate> visitorList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisitorSummarybydate>(VisitorSummarybydate.class));

        GetVisitorSummaryResponseData responseData = new GetVisitorSummaryResponseData();

        Optional<VisitorSummarybydate> optionalVisitorOld = visitorList.stream().filter(f -> f.getUserType().equalsIgnoreCase("old")).findAny();
        if (optionalVisitorOld.isPresent()) {
            FlowSummary flowSummary = assemblyFlowSummary(optionalVisitorOld.get());
            responseData.setOldVisitor(flowSummary);

        }
        Optional<VisitorSummarybydate> optionalVisitorNew = visitorList.stream().filter(f -> f.getUserType().equalsIgnoreCase("new")).findAny();
        if (optionalVisitorNew.isPresent()) {
            FlowSummary flowSummary = assemblyFlowSummary(optionalVisitorNew.get());
            responseData.setNewVisitor(flowSummary);

        }
        response.setData(responseData);
        return response;
    }

    @Override
    public GetVisitUriResponse getVisitUri(GetVisitUriRequest getVisitUriRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from visituri_summary_bydate";
        String where = "";

        List<String> channelList = new ArrayList<>();
        if (getVisitUriRequest.getChannel() != null && !getVisitUriRequest.getChannel().isEmpty()) {
            for (String channel : getVisitUriRequest.getChannel()) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
        }
        if (channelList.isEmpty()) {
            channelList.add("all");
        }
        where += " and lib in (:channel)";
        paramMap.addValue("channel", channelList);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String startTime = this.yMdFORMAT.get().format(System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY);
        if (StringUtils.isNotBlank(getVisitUriRequest.getStartTime())) {
            startTime = getVisitUriRequest.getStartTime();
        }
        where += " and stat_date>=:starttime";
        paramMap.addValue("starttime", startTime);

        String endTime = this.yMdFORMAT.get().format(now);
        if (StringUtils.isNotBlank(getVisitUriRequest.getEndTime())) {
            endTime = getVisitUriRequest.getEndTime();
        }
        where += " and stat_date<=:endtime";
        paramMap.addValue("endtime", endTime);

        String projectName = getVisitUriRequest.getProjectName();
        if (StringUtils.isBlank(projectName)) {
            projectName = "gpapp";
        }
        where += " and project_name=:project";
        paramMap.addValue("project", projectName);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " order by pv desc limit 10";

        List<VisituriSummarybydate> uriList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisituriSummarybydate>(VisituriSummarybydate.class));

        GetVisitUriResponse response = new GetVisitUriResponse();
        List<GetVisitUriResponseData> visitUriResponseDataList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        for (VisituriSummarybydate visituriSummarybydate : uriList) {
            GetVisitUriResponseData getVisitUriResponseData = new GetVisitUriResponseData();
            getVisitUriResponseData.setUri(visituriSummarybydate.getUri());
            getVisitUriResponseData.setPv(visituriSummarybydate.getPv());
            getVisitUriResponseData.setPercent(Float.parseFloat(decimalFormat.format(visituriSummarybydate.getRate())));
            getVisitUriResponseData.setChannel(visituriSummarybydate.getLib());
            visitUriResponseDataList.add(getVisitUriResponseData);
        }
        response.setData(visitUriResponseDataList);
        return response;
    }

    @Override
    public GetSearchWordResponse getSearchWord(GetSearchWordRequest getSearchWordRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from searchword_summary_bydate";
        String where = "";

        List<String> channelList = new ArrayList<>();
        if (getSearchWordRequest.getChannel() != null && !getSearchWordRequest.getChannel().isEmpty()) {
            for (String channel : getSearchWordRequest.getChannel()) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
        }
        if (channelList.isEmpty()) {
            channelList.add("all");
        }
        where += " and lib in (:channel)";
        paramMap.addValue("channel", channelList);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String startTime = this.yMdFORMAT.get().format(System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY);
        if (StringUtils.isNotBlank(getSearchWordRequest.getStartTime())) {
            startTime = getSearchWordRequest.getStartTime();
        }
        where += " and stat_date>=:starttime";
        paramMap.addValue("starttime", startTime);

        String endTime = this.yMdFORMAT.get().format(now);
        if (StringUtils.isNotBlank(getSearchWordRequest.getEndTime())) {
            endTime = getSearchWordRequest.getEndTime();
        }
        where += " and stat_date<=:endtime";
        paramMap.addValue("endtime", endTime);

        String projectName = getSearchWordRequest.getProjectName();
        if (StringUtils.isBlank(projectName)) {
            projectName = "gpapp";
        }
        where += " and project_name=:project";
        paramMap.addValue("project", projectName);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " order by pv desc limit 10";

        List<SearchwordSummarybydate> searchwordList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SearchwordSummarybydate>(SearchwordSummarybydate.class));

        GetSearchWordResponse response = new GetSearchWordResponse();
        List<GetSearchWordResponseData> visitUriResponseDataList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        for (SearchwordSummarybydate searchwordSummarybydate : searchwordList) {
            GetSearchWordResponseData getSearchWordResponseData = new GetSearchWordResponseData();
            getSearchWordResponseData.setWord(searchwordSummarybydate.getSearchword());
            getSearchWordResponseData.setPv(searchwordSummarybydate.getPv());
            getSearchWordResponseData.setPercent(Float.parseFloat(decimalFormat.format(searchwordSummarybydate.getRate())));
            getSearchWordResponseData.setChannel(searchwordSummarybydate.getLib());
            visitUriResponseDataList.add(getSearchWordResponseData);
        }
        response.setData(visitUriResponseDataList);
        return response;
    }

    @Override
    public GetSourceWebsiteResponse getSourceWebsite(GetSourceWebsiteRequest getSourceWebsiteRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from sourcesite_summary_bydate";
        String where = "";

        List<String> channelList = new ArrayList<>();
        if (getSourceWebsiteRequest.getChannel() != null && !getSourceWebsiteRequest.getChannel().isEmpty()) {
            for (String channel : getSourceWebsiteRequest.getChannel()) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
        }
        if (channelList.isEmpty()) {
            channelList.add("all");
        }
        where += " and lib in (:channel)";
        paramMap.addValue("channel", channelList);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String startTime = this.yMdFORMAT.get().format(System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY);
        if (StringUtils.isNotBlank(getSourceWebsiteRequest.getStartTime())) {
            startTime = getSourceWebsiteRequest.getStartTime();
        }
        where += " and stat_date>=:starttime";
        paramMap.addValue("starttime", startTime);

        String endTime = this.yMdFORMAT.get().format(now);
        if (StringUtils.isNotBlank(getSourceWebsiteRequest.getEndTime())) {
            endTime = getSourceWebsiteRequest.getEndTime();
        }
        where += " and stat_date<=:endtime";
        paramMap.addValue("endtime", endTime);

        String projectName = getSourceWebsiteRequest.getProjectName();
        if (StringUtils.isBlank(projectName)) {
            projectName = "gpapp";
        }
        where += " and project_name=:project";
        paramMap.addValue("project", projectName);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " order by pv desc limit 10";

        List<SourcesiteSummarybydate> sourcesiteSummarybydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SourcesiteSummarybydate>(SourcesiteSummarybydate.class));

        GetSourceWebsiteResponse response = new GetSourceWebsiteResponse();
        List<GetSourceWebsiteResponseData> getSourceWebsiteResponseDataList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        for (SourcesiteSummarybydate sourcesiteSummarybydate : sourcesiteSummarybydateList) {
            GetSourceWebsiteResponseData getSourceWebsiteResponseData = new GetSourceWebsiteResponseData();
            getSourceWebsiteResponseData.setWebsite(sourcesiteSummarybydate.getSourcesite());
            getSourceWebsiteResponseData.setPv(sourcesiteSummarybydate.getPv());
            getSourceWebsiteResponseData.setPercent(Float.parseFloat(decimalFormat.format(sourcesiteSummarybydate.getRate())));
            getSourceWebsiteResponseData.setChannel(sourcesiteSummarybydate.getLib());
            getSourceWebsiteResponseDataList.add(getSourceWebsiteResponseData);
        }
        response.setData(getSourceWebsiteResponseDataList);
        return response;
    }

    @Override
    public GetAreaResponse getArea(GetVisitorSummaryRequest getAreaRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from area_summary_bydate";
        String where = " and country<>'all' and province<>'all'";

        List<String> channelList = new ArrayList<>();
        if (getAreaRequest.getChannel() != null && !getAreaRequest.getChannel().isEmpty()) {
            for (String channel : getAreaRequest.getChannel()) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
        }
        if (channelList.isEmpty()) {
            channelList.add("all");
        }
        where += " and lib in (:channel)";
        paramMap.addValue("channel", channelList);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String startTime = this.yMdFORMAT.get().format(System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY);
        if (StringUtils.isNotBlank(getAreaRequest.getStartTime())) {
            startTime = getAreaRequest.getStartTime();
        }
        where += " and stat_date>=:starttime";
        paramMap.addValue("starttime", startTime);

        String endTime = this.yMdFORMAT.get().format(now);
        if (StringUtils.isNotBlank(getAreaRequest.getEndTime())) {
            endTime = getAreaRequest.getEndTime();
        }
        where += " and stat_date<=:endtime";
        paramMap.addValue("endtime", endTime);

        String projectName = getAreaRequest.getProjectName();
        if (StringUtils.isBlank(projectName)) {
            projectName = "gpapp";
        }
        where += " and project_name=:project";
        paramMap.addValue("project", projectName);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " order by pv desc limit 10";

        List<AreaSummarybydate> areaSummarybydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<AreaSummarybydate>(AreaSummarybydate.class));

        GetAreaResponse response = new GetAreaResponse();
        List<GetAreaResponseData> getAreaResponseDataList = new ArrayList<>();

        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        for (AreaSummarybydate areaSummarybydate : areaSummarybydateList) {
            GetAreaResponseData getAreaResponseData = new GetAreaResponseData();
            getAreaResponseData.setProvince(areaSummarybydate.getProvince());
            getAreaResponseData.setPv(areaSummarybydate.getPv());
            getAreaResponseData.setPercent(Float.parseFloat(decimalFormat.format(areaSummarybydate.getRate())));
            getAreaResponseData.setChannel(areaSummarybydate.getLib());
            getAreaResponseData.setStatDate(yMdFORMAT.get().format(areaSummarybydate.getStatDate()));
            getAreaResponseDataList.add(getAreaResponseData);
        }
        response.setData(getAreaResponseDataList);
        return response;
    }

    private GetFlowResponseData getFlowBydate(GetFlowRequest getFlowRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from flow_summary_bydate";
        String where = "";
        List<String> channelList = new ArrayList<>();
        if (getFlowRequest.getChannel() != null && !getFlowRequest.getChannel().isEmpty()) {
            for (String channel : getFlowRequest.getChannel()) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
        }
        if (channelList.isEmpty()) {
            channelList.add("all");
        }
        where += " and lib in (:channel)";
        paramMap.addValue("channel", channelList);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String startTime = this.yMdFORMAT.get().format(System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY);
        if (StringUtils.isNotBlank(getFlowRequest.getStartTime())) {
            startTime = getFlowRequest.getStartTime();
        }
        where += " and stat_date>=:starttime";
        paramMap.addValue("starttime", startTime);

        String endTime = this.yMdFORMAT.get().format(now);
        if (StringUtils.isNotBlank(getFlowRequest.getEndTime())) {
            endTime = getFlowRequest.getEndTime();
        }
        where += " and stat_date<=:endtime";
        paramMap.addValue("endtime", endTime);

        String projectName = getFlowRequest.getProjectName();
        if (StringUtils.isBlank(projectName)) {
            projectName = "gpapp";
        }
        where += " and project_name=:project";
        paramMap.addValue("project", projectName);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }

        List<FlowSummarybydate> flowSummaryList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<FlowSummarybydate>(FlowSummarybydate.class));

        GetFlowResponseData responseData = new GetFlowResponseData();
        Timestamp current = Timestamp.valueOf(endTime + " 00:00:00");
        Optional<FlowSummarybydate> optionalCurrent = flowSummaryList.stream().filter(f -> f.getStatDate().equals(current)).findAny();
        if (optionalCurrent.isPresent()) {
            FlowSummary flowSummary = assemblyFlowSummary(optionalCurrent.get());
            responseData.setCurrent(flowSummary);

        }
        Timestamp previous = Timestamp.valueOf(startTime + " 00:00:00");
        Optional<FlowSummarybydate> optionalPrevious = flowSummaryList.stream().filter(f -> f.getStatDate().equals(previous)).findAny();
        if (optionalPrevious.isPresent()) {
            FlowSummary flowSummary = assemblyFlowSummary(optionalPrevious.get());
            responseData.setPrevious(flowSummary);
        }
        return responseData;
    }

    private FlowSummary assemblyFlowSummary(FlowSummarybydate flowSummarybydate) {
        FlowSummary flowSummary = new FlowSummary();
        flowSummary.setPv(flowSummarybydate.getPv());
        flowSummary.setIpCount(flowSummarybydate.getIpCount());
        flowSummary.setVisit(flowSummarybydate.getVisitCount());
        flowSummary.setUv(flowSummarybydate.getUv());
        flowSummary.setAvgPv(0);
        flowSummary.setAvgVisitTime(0);
        flowSummary.setBounceRate(0);
        flowSummary.setChannel(flowSummarybydate.getLib());
        if (flowSummarybydate.getVisitCount() > 0) {
            DecimalFormat decimalFormat = new DecimalFormat("0.##");

            float avgPv = flowSummarybydate.getPv() * 1.0f / flowSummarybydate.getVisitCount();
            flowSummary.setAvgPv(Float.parseFloat(decimalFormat.format(avgPv)));

            float avgVisitTime = flowSummarybydate.getVisitTime() * 1.0f / flowSummarybydate.getVisitCount();
            flowSummary.setAvgVisitTime(Float.parseFloat(decimalFormat.format(avgVisitTime)));

            float bounceRate = flowSummarybydate.getBounceCount() * 1.0f / flowSummarybydate.getVisitCount();
            flowSummary.setBounceRate(Float.parseFloat(decimalFormat.format(bounceRate)));
        }
        return flowSummary;
    }

    private FlowSummary assemblyFlowSummary(VisitorSummarybydate visitorSummarybydate) {
        FlowSummary flowSummary = new FlowSummary();
        flowSummary.setPv(visitorSummarybydate.getPv());
        flowSummary.setIpCount(visitorSummarybydate.getIpCount());
        flowSummary.setVisit(visitorSummarybydate.getVisitCount());
        flowSummary.setUv(visitorSummarybydate.getUv());
        flowSummary.setAvgPv(0);
        flowSummary.setAvgVisitTime(0);
        flowSummary.setBounceRate(0);
        flowSummary.setChannel(visitorSummarybydate.getLib());
        if (visitorSummarybydate.getVisitCount() > 0) {
            DecimalFormat decimalFormat = new DecimalFormat("0.##");

            float avgPv = visitorSummarybydate.getPv() * 1.0f / visitorSummarybydate.getVisitCount();
            flowSummary.setAvgPv(Float.parseFloat(decimalFormat.format(avgPv)));

            float avgVisitTime = visitorSummarybydate.getVisitTime() * 1.0f / visitorSummarybydate.getVisitCount();
            flowSummary.setAvgVisitTime(Float.parseFloat(decimalFormat.format(avgVisitTime)));

            float bounceRate = visitorSummarybydate.getBounceCount() * 1.0f / visitorSummarybydate.getVisitCount();
            flowSummary.setBounceRate(Float.parseFloat(decimalFormat.format(bounceRate)));
        }
        return flowSummary;
    }

    @Override
    public GetFlowTrendDetailResponse getFlowTrendDetail(GetFlowTrendDetailRequest getFlowTrendDetailRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from flow_trend_bydate";
        String where = "";

        List<String> channelList = new ArrayList<>();
        if (getFlowTrendDetailRequest.getChannel() != null && !getFlowTrendDetailRequest.getChannel().isEmpty()) {
            for (String channel : getFlowTrendDetailRequest.getChannel()) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
        }
        if (channelList.isEmpty()) {
            channelList.add("all");
        }
        where += " and lib in (:channel)";
        paramMap.addValue("channel", channelList);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp startTime = Timestamp.valueOf(this.yMdFORMAT.get().format(now) + " 00:00:00");
        if (StringUtils.isNotBlank(getFlowTrendDetailRequest.getStartTime())) {
            startTime = Timestamp.valueOf(getFlowTrendDetailRequest.getStartTime() + " 00:00:00");
        }
        where += " and stat_date>=:starttime";
        paramMap.addValue("starttime", this.yMdFORMAT.get().format(startTime));

        Timestamp endTime = startTime;
        if (StringUtils.isNotBlank(getFlowTrendDetailRequest.getEndTime())) {
            endTime = Timestamp.valueOf(getFlowTrendDetailRequest.getEndTime() + " 00:00:00");
        }
        where += " and stat_date<=:endtime";
        paramMap.addValue("endtime", this.yMdFORMAT.get().format(endTime));

        String projectName = getFlowTrendDetailRequest.getProjectName();
        if (StringUtils.isBlank(projectName)) {
            projectName = "gpapp";
        }
        where += " and project_name=:project";
        paramMap.addValue("project", projectName);

        String visitorType = getFlowTrendDetailRequest.getVisitorType();
        if (StringUtils.isNotBlank(visitorType)) {
            if ("老访客".equalsIgnoreCase(visitorType)) {
                visitorType = "false";
            } else if ("新访客".equalsIgnoreCase(visitorType)) {
                visitorType = "true";
            }

        } else {
            visitorType = "all";
        }
        where += " and is_first_day=:is_first_day";
        paramMap.addValue("is_first_day", visitorType);

        List<String> countryList = getFlowTrendDetailRequest.getCountry();
        if (countryList == null) {
            countryList = new ArrayList<>();
        }

        if (countryList.isEmpty()) {
            countryList.add("all");
        }
        where += " and country in (:country)";
        paramMap.addValue("country", countryList);

        List<String> provinceList = getFlowTrendDetailRequest.getProvince();
        if (provinceList == null) {
            provinceList = new ArrayList<>();
        }

        if (provinceList.isEmpty()) {
            provinceList.add("all");
        }
        where += " and province in (:province)";
        paramMap.addValue("province", provinceList);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " order by stat_date";

        List<FlowTrendbydate> flowTrendbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<FlowTrendbydate>(FlowTrendbydate.class));

        GetFlowTrendDetailResponse response = new GetFlowTrendDetailResponse();
        List<FlowDetail> flowDetailList = new ArrayList<>();

        FlowDetail totalFlowDetail = null;
        if (!flowTrendbydateList.isEmpty()) {
            totalFlowDetail = new FlowDetail();
            totalFlowDetail.setAvgPv(0);
            totalFlowDetail.setAvgVisitTime(0);
            totalFlowDetail.setBounceRate(0);
        }
        int totalVisitTime = 0;
        int totalBounceCount = 0;

        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        for (FlowTrendbydate flowTrendbydate : flowTrendbydateList) {
            // 合计
            totalFlowDetail.setStatDate(this.yMdFORMAT.get().format(flowTrendbydate.getStatDate()));
            totalFlowDetail.setPv(totalFlowDetail.getPv() + flowTrendbydate.getPv());
            totalFlowDetail.setIpCount(totalFlowDetail.getIpCount() + flowTrendbydate.getIpCount());
            totalFlowDetail.setVisitCount(totalFlowDetail.getVisitCount() + flowTrendbydate.getVisitCount());
            totalFlowDetail.setUv(totalFlowDetail.getUv() + flowTrendbydate.getUv());
            totalFlowDetail.setNewUv(totalFlowDetail.getNewUv() + flowTrendbydate.getNewUv());
            totalFlowDetail.setChannel(flowTrendbydate.getLib());
            totalVisitTime += flowTrendbydate.getVisitTime();
            totalBounceCount += flowTrendbydate.getBounceCount();

            //每天的数量
            FlowDetail flowDetail = new FlowDetail();
            flowDetail.setStatDate(this.yMdFORMAT.get().format(flowTrendbydate.getStatDate()));
            flowDetail.setPv(flowTrendbydate.getPv());
            flowDetail.setIpCount(flowTrendbydate.getIpCount());
            flowDetail.setVisitCount(flowTrendbydate.getVisitCount());
            flowDetail.setUv(flowTrendbydate.getUv());
            flowDetail.setNewUv(flowTrendbydate.getNewUv());
            flowDetail.setChannel(flowTrendbydate.getLib());
            flowDetail.setAvgPv(0);
            flowDetail.setAvgVisitTime(0);
            flowDetail.setBounceRate(0);

            if (flowDetail.getVisitCount() > 0) {

                float avgPv = flowTrendbydate.getPv() * 1.0f / flowTrendbydate.getVisitCount();
                flowDetail.setAvgPv(Float.parseFloat(decimalFormat.format(avgPv)));

                float avgVisitTime = flowTrendbydate.getVisitTime() * 1.0f / flowTrendbydate.getVisitCount();
                flowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.format(avgVisitTime)));

                float bounceRate = flowTrendbydate.getBounceCount() * 1.0f / flowDetail.getVisitCount();
                flowDetail.setBounceRate(Float.parseFloat(decimalFormat.format(bounceRate)));
            }

            flowDetailList.add(flowDetail);
        }
        GetFlowTrendDetailResponseData responseData = new GetFlowTrendDetailResponseData();
        responseData.setDetail(flowDetailList);

        if (totalFlowDetail != null && totalFlowDetail.getVisitCount() > 0) {

            float avgPv = totalFlowDetail.getPv() * 1.0f / totalFlowDetail.getVisitCount();
            totalFlowDetail.setAvgPv(Float.parseFloat(decimalFormat.format(avgPv)));

            float avgVisitTime = totalVisitTime * 1.0f / totalFlowDetail.getVisitCount();
            totalFlowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.format(avgVisitTime)));

            float bounceRate = totalBounceCount * 1.0f / totalFlowDetail.getVisitCount();
            totalFlowDetail.setBounceRate(Float.parseFloat(decimalFormat.format(bounceRate)));
        }
        responseData.setTotal(totalFlowDetail);


        response.setData(responseData);
        return response;
    }
}
