package com.zcunsoft.tracking.api.services;

import com.zcunsoft.tracking.api.entity.clickhouse.FlowSummarybydate;
import com.zcunsoft.tracking.api.entity.clickhouse.FlowSummarybyhour;
import com.zcunsoft.tracking.api.entity.clickhouse.VisitorSummarybydate;
import com.zcunsoft.tracking.api.models.enums.LibType;
import com.zcunsoft.tracking.api.models.summary.*;
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

        Optional<VisitorSummarybydate> optionalVisitorOld= visitorList.stream().filter(f -> f.getUserType().equalsIgnoreCase("old")).findAny();
        if (optionalVisitorOld.isPresent()) {
            FlowSummary flowSummary = assemblyFlowSummary(optionalVisitorOld.get());
            responseData.setOldVisitor(flowSummary);

        }
        Optional<VisitorSummarybydate> optionalVisitorNew= visitorList.stream().filter(f -> f.getUserType().equalsIgnoreCase("new")).findAny();
        if (optionalVisitorNew.isPresent()) {
            FlowSummary flowSummary = assemblyFlowSummary(optionalVisitorNew.get());
            responseData.setNewVisitor(flowSummary);

        }
        response.setData(responseData);
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
        Timestamp current= Timestamp.valueOf(endTime + " 00:00:00");
        Optional<FlowSummarybydate> optionalCurrent= flowSummaryList.stream().filter(f -> f.getStatDate().equals(current)).findAny();
        if (optionalCurrent.isPresent()) {
            FlowSummary flowSummary = assemblyFlowSummary(optionalCurrent.get());
            responseData.setCurrent(flowSummary);

        }
        Timestamp previous= Timestamp.valueOf(startTime + " 00:00:00");
        Optional<FlowSummarybydate> optionalPrevious= flowSummaryList.stream().filter(f -> f.getStatDate().equals(previous)).findAny();
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
}
