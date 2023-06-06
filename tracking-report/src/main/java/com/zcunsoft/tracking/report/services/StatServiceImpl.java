package com.zcunsoft.tracking.report.services;

import com.alibaba.fastjson2.JSONObject;
import com.zcunsoft.common.utils.DateUtils;
import com.zcunsoft.tracking.report.cfg.ServiceSetting;
import com.zcunsoft.tracking.report.model.AppVersion;
import com.zcunsoft.tracking.report.model.DownloadChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatServiceImpl implements IStatService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NamedParameterJdbcTemplate clickHouseJdbcTemplate;

    private final NamedParameterJdbcTemplate mysqlJdbcTemplate;


    public StatServiceImpl(NamedParameterJdbcTemplate clickHouseJdbcTemplate, NamedParameterJdbcTemplate mysqlJdbcTemplate, ServiceSetting setting) {
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
        this.mysqlJdbcTemplate = mysqlJdbcTemplate;
    }

    @Override
    public void statAppVersion() {
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            String sql = "select distinct app_version as version,'sensor_user_report_bydate' as stat_table from sensor_user_report_bydate";
            List<AppVersion> versionList = clickHouseJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<AppVersion>(AppVersion.class));

            sql = "select distinct app_version as version,'sensor_user_retention_bydate' as stat_table from sensor_user_retention_bydate";
            versionList.addAll(clickHouseJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<AppVersion>(AppVersion.class)));

            sql = "select distinct app_version as version,'sensor_user_life_report_bydate' as stat_table from sensor_user_life_report_bydate";
            versionList.addAll(clickHouseJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<AppVersion>(AppVersion.class)));

            sql = "select distinct app_version as version,'sensor_user_report_byarea' as stat_table from sensor_user_report_byarea";
            versionList.addAll(clickHouseJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<AppVersion>(AppVersion.class)));

            String updatesql = "update tbl_stat_appversion set update_time=now(),label=:label,ordernum=:ordernum where version=:version and stat_table=:stat_table";
            for (AppVersion version : versionList) {
                MapSqlParameterSource updateMap = new MapSqlParameterSource();
                updateMap.addValue("stat_table", version.getStatTable());
                String label = version.getVersion();
                int ordernum = 3;
                if (StringUtils.isBlank(label)) {
                    label = "无";
                    version.setVersion(label);
                    ordernum = 1;
                } else if ("all".equalsIgnoreCase(label)) {
                    label = "全部";
                    ordernum = 0;
                }
                updateMap.addValue("version", version.getVersion());
                updateMap.addValue("label", label);
                updateMap.addValue("ordernum", ordernum);
                int affected = this.mysqlJdbcTemplate.update(updatesql, updateMap);
                if (affected == 0) {
                    updatesql = "insert into tbl_stat_appversion (version,label,ordernum,stat_table,update_time) " +
                            "values ( :version,:label,:ordernum,:stat_table,now())";
                    this.mysqlJdbcTemplate.update(updatesql, updateMap);
                }
            }

        } catch (Exception ex) {
            logger.error("statAppVersion ", ex);
        }
    }

    @Override
    public void statDownloadChannel() {
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            String sql = "select distinct download_channel as channel,'sensor_user_report_bydate' as stat_table from sensor_user_report_bydate";
            List<DownloadChannel> downloadChannelList = clickHouseJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<DownloadChannel>(DownloadChannel.class));

            sql = "select distinct download_channel as channel,'sensor_user_retention_bydate' as stat_table from sensor_user_retention_bydate";
            downloadChannelList.addAll(clickHouseJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<DownloadChannel>(DownloadChannel.class)));

            sql = "select distinct download_channel as channel,'sensor_user_life_report_bydate' as stat_table from sensor_user_life_report_bydate";
            downloadChannelList.addAll(clickHouseJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<DownloadChannel>(DownloadChannel.class)));

            sql = "select distinct download_channel as channel,'sensor_user_report_byarea' as stat_table from sensor_user_report_byarea";
            downloadChannelList.addAll(clickHouseJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<DownloadChannel>(DownloadChannel.class)));

            String updatesql = "update tbl_stat_downloadchannel set update_time=now(),label=:label,ordernum=:ordernum where channel=:channel and stat_table=:stat_table";
            for (DownloadChannel downloadChannel : downloadChannelList) {
                MapSqlParameterSource updateMap = new MapSqlParameterSource();
                updateMap.addValue("stat_table", downloadChannel.getStatTable());
                String label = downloadChannel.getChannel();
                int ordernum = 3;
                if (StringUtils.isBlank(label)) {
                    label = "无";
                    downloadChannel.setChannel(label);
                    ordernum = 1;
                } else if ("all".equalsIgnoreCase(label)) {
                    label = "全部";
                    ordernum = 0;
                }
                updateMap.addValue("channel", downloadChannel.getChannel());
                updateMap.addValue("label", label);
                updateMap.addValue("ordernum", ordernum);
                int affected = this.mysqlJdbcTemplate.update(updatesql, updateMap);
                if (affected == 0) {
                    updatesql = "insert into tbl_stat_downloadchannel (channel,label,ordernum,stat_table,update_time) " +
                            "values ( :channel,:label,:ordernum,:stat_table,now())";
                    this.mysqlJdbcTemplate.update(updatesql, updateMap);
                }
            }

        } catch (Exception ex) {
            logger.error("statAppVersion ", ex);
        }
    }

}
