package com.zcunsoft.tracking.api.services;

import com.zcunsoft.tracking.api.entity.clickhouse.FlowSummarybydate;
import com.zcunsoft.tracking.api.models.enums.LibType;
import com.zcunsoft.tracking.api.models.summary.FlowSummary;
import com.zcunsoft.tracking.api.models.summary.GetFlowRequest;
import com.zcunsoft.tracking.api.models.summary.GetFlowResponse;
import com.zcunsoft.tracking.api.models.summary.GetFlowResponseData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

    private GetFlowResponseData getFlowBydate(GetFlowRequest getFlowRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from flow_summary_bydate";
        String where = "";

        if (getFlowRequest.getChannel() != null && !getFlowRequest.getChannel().isEmpty()) {
            List<String> channelList = new ArrayList<>();
            for (String channel : getFlowRequest.getChannel()) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
            if (channelList.isEmpty()) {
                channelList.add("all");
            }
            where += " and lib in (:channel)";
            paramMap.addValue("channel", channelList);
        }

        if (StringUtils.isNotBlank(getFlowRequest.getStartTime())) {
            where += " and stat_date>=:starttime";
            paramMap.addValue("starttime", getFlowRequest.getStartTime());
        }
        if (StringUtils.isNotBlank(getFlowRequest.getEndTime())) {
            where += " and stat_date<=:endtime";
            paramMap.addValue("endtime", getFlowRequest.getEndTime());
        }

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
        if (!flowSummaryList.isEmpty()) {
            FlowSummarybydate flowSummarybydate = flowSummaryList.get(0);
            FlowSummary flowSummary = new FlowSummary();
            flowSummary.setPv(flowSummarybydate.getPv());
            flowSummary.setIpCount(flowSummarybydate.getIpCount());
            flowSummary.setVisit(flowSummarybydate.getVisitCount());
            flowSummary.setUv(flowSummarybydate.getUv());
            flowSummary.setAvgPv(0);
            flowSummary.setAvgVisitTime(0);
            flowSummary.setBounceRate(0);
            if (flowSummarybydate.getVisitCount() > 0) {
                DecimalFormat decimalFormat = new DecimalFormat("0.##");

                float avgPv = flowSummarybydate.getPv() * 1.0f / flowSummarybydate.getVisitCount();
                flowSummary.setAvgPv(Float.parseFloat(decimalFormat.format(avgPv)));

                float avgVisitTime = flowSummarybydate.getVisitTime() * 1.0f / flowSummarybydate.getVisitCount();
                flowSummary.setAvgVisitTime(Float.parseFloat(decimalFormat.format(avgVisitTime)));

                float bounceRate = flowSummarybydate.getBounceCount() * 1.0f / flowSummarybydate.getVisitCount();
                flowSummary.setBounceRate(Float.parseFloat(decimalFormat.format(bounceRate)));
            }
            responseData.setCurrent(flowSummary);

        }
        return responseData;
    }
}
