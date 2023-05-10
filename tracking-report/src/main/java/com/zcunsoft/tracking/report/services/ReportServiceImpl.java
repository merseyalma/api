package com.zcunsoft.tracking.report.services;

import com.zcunsoft.common.core.domain.entity.SysRole;
import com.zcunsoft.common.core.domain.model.LoginUser;
import com.zcunsoft.common.utils.DateUtils;
import com.zcunsoft.common.utils.SecurityUtils;
import com.zcunsoft.tracking.report.entity.clickhouse.*;
import com.zcunsoft.tracking.report.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements IReportService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final NamedParameterJdbcTemplate clickHouseJdbcTemplate;

    private final NamedParameterJdbcTemplate mysqlJdbcTemplate;

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

    public ReportServiceImpl(NamedParameterJdbcTemplate clickHouseJdbcTemplate, NamedParameterJdbcTemplate mysqlJdbcTemplate) {
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
        this.mysqlJdbcTemplate = mysqlJdbcTemplate;
    }

    @Override
    public QueryUserStatResponse getUserStatlist(QueryUserStatRequest queryUserStatRequest) {
        QueryUserStatResponse response = new QueryUserStatResponse();
        try {
//            Sort sort = Sort.by(Sort.Direction.DESC, "statDate");
//            Pageable pageable = PageRequest.of(queryUserStatRequest.getPageNum() - 1, queryUserStatRequest.getPageSize(), sort);
//
//            Specification<UserReportBydate> spec = (root, query, cb) -> {
//                List<Predicate> predicates = new ArrayList<Predicate>();
//
//                if (StringUtils.isNotBlank(queryUserStatRequest.getChannel())) {
//                    predicates.add(cb.equal(root.get("lib"), queryUserStatRequest.getChannel()));
//                }
//                if (StringUtils.isNotBlank(queryUserStatRequest.getStartTime())) {
//                    predicates.add(cb.greaterThan(root.get("statDate"), Timestamp.valueOf(queryUserStatRequest.getStartTime())));
//                }
//                if (StringUtils.isNotBlank(queryUserStatRequest.getEndTime())) {
//                    predicates.add(cb.lessThan(root.get("statDate"), Timestamp.valueOf(queryUserStatRequest.getEndTime())));
//                }
//                Predicate[] pre = new Predicate[predicates.size()];
//                return cb.and(predicates.toArray(pre));
//            };
//            Page<UserReportBydate> placePageList = this.userReportBydateRepository.findAll(spec, pageable);
            MapSqlParameterSource paramMap = new MapSqlParameterSource();

            String getListSql = "select * from sensor_user_report_bydate";
            String getCountSql = "select count(*) from sensor_user_report_bydate";
            String order = " order by stat_date desc";
            String where = "";

            if (StringUtils.isNotBlank(queryUserStatRequest.getChannel())) {
                where += " and lib=:channel";
                paramMap.addValue("channel", queryUserStatRequest.getChannel());
            }
            if (StringUtils.isNotBlank(queryUserStatRequest.getDownloadChannel())) {
                where += " and download_channel=:download_channel";
                String downloadChannel = queryUserStatRequest.getDownloadChannel();
                if ("无".equalsIgnoreCase(downloadChannel)) {
                    downloadChannel = "";
                }

                paramMap.addValue("download_channel", downloadChannel);
            }
            if (StringUtils.isNotBlank(queryUserStatRequest.getAppVersion())) {
                where += " and app_version=:app_version";
                String version = queryUserStatRequest.getAppVersion();
                if ("无".equalsIgnoreCase(version)) {
                    version = "";
                }
                paramMap.addValue("app_version", version);
            }
            if (StringUtils.isNotBlank(queryUserStatRequest.getStartTime())) {
                where += " and stat_date>=:starttime ";
                paramMap.addValue("starttime", queryUserStatRequest.getStartTime());
            }
            if (StringUtils.isNotBlank(queryUserStatRequest.getEndTime())) {
                where += " and stat_date<=:endtime ";
                paramMap.addValue("endtime", queryUserStatRequest.getEndTime());
            }

            String projectName = filterProject(queryUserStatRequest.getProjectName());
            if (StringUtils.isNotBlank(projectName)) {
                where += " and project_name=:project ";
                paramMap.addValue("project", projectName);
            }

            if (StringUtils.isNotBlank(where)) {
                getListSql += " where " + where.substring(4);
                getCountSql += " where " + where.substring(4);
            }
            getListSql += order + " limit " + (queryUserStatRequest.getPageNum() - 1) * queryUserStatRequest.getPageSize() + "," + queryUserStatRequest.getPageSize();


            Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
            List<UserReportBydate> userStatList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserReportBydate>(UserReportBydate.class));

            QueryUserStatResponseData responseData = new QueryUserStatResponseData();


            responseData.setRows(userStatList);
            responseData.setTotal(total);
            response.setData(responseData);

        } catch (Exception ex) {
            logger.error("getUserStatlist error," + ex.getMessage());
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public QueryVisitResponse getVisitlist(QueryVisitRequest queryVisitRequest) {
        QueryVisitResponse response = new QueryVisitResponse();
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();

            String getListSql = "select * from sensor_pageview_report_bydate";
            String getCountSql = "select count(*) from sensor_pageview_report_bydate";
            String order = " order by stat_date desc";
            String where = "";

            if (StringUtils.isNotBlank(queryVisitRequest.getChannel())) {
                where += " and lib=:channel";
                paramMap.addValue("channel", queryVisitRequest.getChannel());
            }
            if (StringUtils.isNotBlank(queryVisitRequest.getStartTime())) {
                where += " and stat_date>=:starttime ";
                paramMap.addValue("starttime", queryVisitRequest.getStartTime());
            }
            if (StringUtils.isNotBlank(queryVisitRequest.getEndTime())) {
                where += " and stat_date<=:endtime ";
                paramMap.addValue("endtime", queryVisitRequest.getEndTime());
            }
            String projectName = filterProject(queryVisitRequest.getProjectName());
            if (StringUtils.isNotBlank(projectName)) {
                where += " and project_name=:project ";
                paramMap.addValue("project", projectName);
            }

            if (StringUtils.isNotBlank(where)) {
                getListSql += " where " + where.substring(4);
                getCountSql += " where " + where.substring(4);
            }
            getListSql += order + " limit " + (queryVisitRequest.getPageNum() - 1) * queryVisitRequest.getPageSize() + "," + queryVisitRequest.getPageSize();


            Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
            List<PageviewReportBydate> viewStatList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<PageviewReportBydate>(PageviewReportBydate.class));

            QueryVisitResponseData responseData = new QueryVisitResponseData();


            responseData.setRows(viewStatList);
            responseData.setTotal(total);
            response.setData(responseData);

        } catch (Exception ex) {
            logger.error("getVisitlist error,", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public RealStatResponse getRealStat(RealStatRequest realStatRequest) {
        RealStatResponse response = new RealStatResponse();
        try {
            String getHourSql = "select * from sensor_user_report_byhour where stat_date=:stat_date";
            String getDateSql = "select * from sensor_user_report_bydate where stat_date=:stat_date";
            String where = "";

            String today = yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis()));
            String mdToday = today.substring(5);
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            paramMap.addValue("stat_date", today);
            if (StringUtils.isNotBlank(realStatRequest.getChannel())) {
                where += " and lib=:channel";
                paramMap.addValue("channel", realStatRequest.getChannel());
            }
            String projectName = filterProject(realStatRequest.getProjectName());
            if (StringUtils.isNotBlank(projectName)) {
                where += " and project_name=:project ";
                paramMap.addValue("project", projectName);
            }
            if (StringUtils.isNotBlank(realStatRequest.getDownloadChannel())) {
                where += " and download_channel=:download_channel";
                String downloadChannel = realStatRequest.getDownloadChannel();
                if ("无".equalsIgnoreCase(downloadChannel)) {
                    downloadChannel = "";
                }

                paramMap.addValue("download_channel", downloadChannel);
            }
            if (StringUtils.isNotBlank(realStatRequest.getAppVersion())) {
                where += " and app_version=:app_version";
                String version = realStatRequest.getAppVersion();
                if ("无".equalsIgnoreCase(version)) {
                    version = "";
                }
                paramMap.addValue("app_version", version);
            }

            if (StringUtils.isNotBlank(where)) {
                getHourSql += where + " order by stat_hour";
                getDateSql += where;
            }
            List<UserReportBydate> dateUserReportStatList = clickHouseJdbcTemplate.query(getDateSql, paramMap, new BeanPropertyRowMapper<UserReportBydate>(UserReportBydate.class));
            List<UserReportByhour> hourUserReportStatList = clickHouseJdbcTemplate.query(getHourSql, paramMap, new BeanPropertyRowMapper<UserReportByhour>(UserReportByhour.class));
            RealStatResponseData responseData = new RealStatResponseData();
            responseData.setToday(new Timestamp(System.currentTimeMillis()));
            if (dateUserReportStatList.size() > 0) {
                UserReportBydate userReportBydate = dateUserReportStatList.get(0);
                responseData.setActive_users(userReportBydate.getActive_users());
                responseData.setNew_pv(userReportBydate.getNew_pv());
                responseData.setOld_users(userReportBydate.getOld_users());
                responseData.setReal_updatetime(userReportBydate.getUpdate_time());
                responseData.setNew_users(userReportBydate.getNew_users());
            }
            List<String> timeList = new ArrayList<>();
            List<Long> new_users_byhour = new ArrayList<>();
            List<Long> old_users_byhour = new ArrayList<>();
            List<Long> active_users_byhour = new ArrayList<>();
            List<Long> new_pv_byhour = new ArrayList<>();

            if (hourUserReportStatList.size() > 0) {
                DecimalFormat df = new DecimalFormat("0.00");

                responseData.setNew_users_byhour_sum(hourUserReportStatList.stream().mapToLong(l -> l.getNew_users()).sum());
                responseData.setNew_users_byhour_avg(Float.valueOf(df.format(hourUserReportStatList.stream().mapToLong(l -> l.getNew_users()).average().getAsDouble())));

                responseData.setOld_users_byhour_sum(hourUserReportStatList.stream().mapToLong(l -> l.getOld_users()).sum());
                responseData.setOld_users_byhour_avg(Float.valueOf(df.format(hourUserReportStatList.stream().mapToLong(l -> l.getOld_users()).average().getAsDouble())));

                responseData.setActive_users_byhour_sum(hourUserReportStatList.stream().mapToLong(l -> l.getActive_users()).sum());
                responseData.setActive_users_byhour_avg(Float.valueOf(df.format(hourUserReportStatList.stream().mapToLong(l -> l.getActive_users()).average().getAsDouble())));

                responseData.setNew_pv_byhour_sum(hourUserReportStatList.stream().mapToLong(l -> l.getNew_pv()).sum());
                responseData.setNew_pv_byhour_avg(Float.valueOf(df.format(hourUserReportStatList.stream().mapToLong(l -> l.getNew_pv()).average().getAsDouble())));
            }
            for (int i = 0; i < 24; i++) {
                String hour = String.valueOf(i + 100).substring(1);
                String yhour = mdToday + " " + hour + ":00";
                timeList.add(yhour);

                Optional<UserReportByhour> optUserReportByhour = hourUserReportStatList.stream().filter(f -> f.getStat_hour().equalsIgnoreCase(hour)).findAny();
                if (optUserReportByhour.isPresent()) {
                    new_users_byhour.add(optUserReportByhour.get().getNew_users());
                    old_users_byhour.add(optUserReportByhour.get().getOld_users());
                    active_users_byhour.add(optUserReportByhour.get().getActive_users());
                    new_pv_byhour.add(optUserReportByhour.get().getNew_pv());
                    responseData.setReal_byhour_updatetime(optUserReportByhour.get().getUpdate_time());
                    responseData.setLatestHour(yhour);
                    responseData.setActive_users_latest(optUserReportByhour.get().getActive_users());
                    responseData.setNew_pv_latest(optUserReportByhour.get().getNew_pv());
                    responseData.setOld_users_latest(optUserReportByhour.get().getOld_users());
                    responseData.setNew_users_latest(optUserReportByhour.get().getNew_users());
                } else {
                    new_users_byhour.add(0L);
                    old_users_byhour.add(0L);
                    active_users_byhour.add(0L);
                    new_pv_byhour.add(0L);
                }
            }
            responseData.setTimes(timeList);
            responseData.setNew_pv_byhour(new_pv_byhour);
            responseData.setActive_users_byhour(active_users_byhour);
            responseData.setNew_users_byhour(new_users_byhour);
            responseData.setOld_users_byhour(old_users_byhour);

            response.setData(responseData);

        } catch (Exception ex) {
            logger.error("getRealStat error,", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public EntireStatResponse getEntireStat(EntireStatRequest entireStatRequest) {
        EntireStatResponse response = new EntireStatResponse();
        try {
            long now = System.currentTimeMillis();
            Timestamp endTime = new Timestamp(now);
            endTime = Timestamp.valueOf(this.yMdFORMAT.get().format(endTime) + " 00:00:00");

            Timestamp start30Time = new Timestamp(endTime.getTime() - 2505600000L);
            Timestamp start7Time = new Timestamp(endTime.getTime() - 518400000L);

            String getDateSql = "select * from sensor_user_report_bydate where stat_date>=:start_date and stat_date<=:end_date";
            String getDateRetentionSql = "select * from sensor_user_retention_bydate where stat_date>=:start_date and stat_date<=:end_date";
            String getWeekRetentionSql = "select * from sensor_user_retention_byweek where stat_date>=:start_date and stat_date<=:end_date";

            String where = "";

            String today = yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis()));
            String mdToday = today.substring(5);
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            paramMap.addValue("start_date", yMdFORMAT.get().format(start30Time));
            paramMap.addValue("end_date", yMdFORMAT.get().format(endTime));
            if (StringUtils.isNotBlank(entireStatRequest.getChannel())) {
                where += " and lib=:channel";
                paramMap.addValue("channel", entireStatRequest.getChannel());
            }
            String projectName = filterProject(entireStatRequest.getProjectName());
            if (StringUtils.isNotBlank(projectName)) {
                where += " and project_name=:project ";
                paramMap.addValue("project", projectName);
            }
            if (StringUtils.isNotBlank(entireStatRequest.getDownloadChannel())) {
                where += " and download_channel=:download_channel";
                String downloadChannel = entireStatRequest.getDownloadChannel();
                if ("无".equalsIgnoreCase(downloadChannel)) {
                    downloadChannel = "";
                }

                paramMap.addValue("download_channel", downloadChannel);
            }
            if (StringUtils.isNotBlank(entireStatRequest.getAppVersion())) {
                where += " and app_version=:app_version";
                String version = entireStatRequest.getAppVersion();
                if ("无".equalsIgnoreCase(version)) {
                    version = "";
                }
                paramMap.addValue("app_version", version);
            }
            if (StringUtils.isNotBlank(where)) {
                getDateSql += where + " order by stat_date";
                getDateRetentionSql += where + " order by stat_date";
                getWeekRetentionSql += where + " order by stat_date";
            }

            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            List<UserReportBydate> dateUserReportStatList = clickHouseJdbcTemplate.query(getDateSql, paramMap, new BeanPropertyRowMapper<UserReportBydate>(UserReportBydate.class));
            EntireStatResponseData responseData = new EntireStatResponseData();
            responseData.setEndTime(endTime);
            responseData.setStart7Time(start7Time);
            responseData.setStart30Time(start30Time);
            if (dateUserReportStatList.size() > 0) {

                responseData.setUser_report_bydate_updatetime(dateUserReportStatList.get(dateUserReportStatList.size() - 1).getUpdate_time());
                responseData.setTotal_new_users_30(dateUserReportStatList.stream().mapToLong(m -> m.getNew_users()).sum());
                responseData.setTotal_active_users_30(dateUserReportStatList.stream().mapToLong(m -> m.getActive_users()).sum());

                List<UserReportBydate> date7UserReportStatList = dateUserReportStatList.stream().filter(f -> f.getStat_date().getTime() >= start7Time.getTime()).collect(Collectors.toList());

                responseData.setTotal_new_users_7(date7UserReportStatList.stream().mapToLong(m -> m.getNew_users()).sum());
                responseData.setTotal_active_users_7(date7UserReportStatList.stream().mapToLong(m -> m.getActive_users()).sum());

                List<String> times = new ArrayList<>();
                List<Long> active_users_30 = new ArrayList<>();
                List<Float> duration_per_user_30 = new ArrayList<>();
                List<Long> new_pv_30 = new ArrayList<>();
                List<Float> duration_per_time_30 = new ArrayList<>();
                List<Long> new_users_30 = new ArrayList<>();
                List<Long> old_users_30 = new ArrayList<>();
                List<Float> new_users_percent_30 = new ArrayList<>();

                for (int i = 0; i < 30; i++) {
                    Timestamp tsDate = new Timestamp(start30Time.getTime() + DateUtils.MILLIS_PER_DAY * i);
                    String sDate = yMdFORMAT.get().format(tsDate);
                    times.add(sDate);

                    Optional<UserReportBydate> optUserReportByDate = dateUserReportStatList.stream().filter(f -> f.getStat_date().getTime() == tsDate.getTime()).findAny();
                    if (optUserReportByDate.isPresent()) {
                        UserReportBydate userReport = optUserReportByDate.get();
                        active_users_30.add(userReport.getActive_users());
                        Float duration_per_user = 0f;
                        if (userReport.getActive_users() > 0) {
                            duration_per_user = Float.valueOf(decimalFormat.format(userReport.getDuration() / userReport.getActive_users()));
                        }
                        duration_per_user_30.add(duration_per_user);
                        new_pv_30.add(userReport.getNew_pv());
                        Float duration_per_time = 0f;
                        if (userReport.getLeave_times() > 0) {
                            duration_per_time = Float.valueOf(decimalFormat.format(userReport.getDuration() / userReport.getLeave_times()));
                        }
                        duration_per_time_30.add(duration_per_time);
                        new_users_30.add(userReport.getNew_users());
                        old_users_30.add(userReport.getOld_users());

                        Float new_users_percent = 0f;
                        if (userReport.getActive_users() > 0) {
                            new_users_percent = Float.valueOf(decimalFormat.format(userReport.getNew_users() * 100.0f / userReport.getActive_users()));
                        }
                        new_users_percent_30.add(new_users_percent);
                        responseData.setLatestDay(yMdFORMAT.get().format(userReport.getStat_date()));
                        responseData.setActive_users_30_latest(userReport.getActive_users());
                        responseData.setDuration_per_user_30_latest(duration_per_user);
                        responseData.setNew_pv_30_latest(userReport.getNew_pv());
                        responseData.setDuration_per_time_30_latest(duration_per_time);
                        responseData.setNew_users_30_latest(userReport.getNew_users());
                        responseData.setOld_users_30_latest(userReport.getOld_users());
                        responseData.setNew_users_percent_30_latest(new_users_percent);

                    } else {
                        active_users_30.add(0L);
                        duration_per_user_30.add(0f);
                        new_pv_30.add(0L);
                        duration_per_time_30.add(0f);
                        new_users_30.add(0L);
                        old_users_30.add(0L);
                        new_users_percent_30.add(0f);
                    }
                }

                responseData.setActive_users_30_sum(responseData.getTotal_active_users_30());
                responseData.setActive_users_30_avg(Float.valueOf(decimalFormat.format(dateUserReportStatList.stream().mapToLong(m -> m.getActive_users()).average().getAsDouble())));

                double totalDuration = dateUserReportStatList.stream().mapToDouble(m -> m.getDuration()).sum();

                long totalActiveUsers = active_users_30.stream().mapToLong(m -> m).sum();
                if (totalActiveUsers > 0) {
                    Double duration_per_user = totalDuration / totalActiveUsers;
                    responseData.setDuration_per_user_30_avg(Float.valueOf(decimalFormat.format(duration_per_user.floatValue())));
                }

                responseData.setNew_pv_30_sum(dateUserReportStatList.stream().mapToLong(m -> m.getNew_pv()).sum());
                responseData.setNew_pv_30_avg(Float.valueOf(decimalFormat.format(dateUserReportStatList.stream().mapToLong(m -> m.getNew_pv()).average().getAsDouble())));

                responseData.setNew_users_30_sum(dateUserReportStatList.stream().mapToLong(m -> m.getNew_users()).sum());
                responseData.setNew_users_30_avg(Float.valueOf(decimalFormat.format(dateUserReportStatList.stream().mapToLong(m -> m.getNew_users()).average().getAsDouble())));

                responseData.setOld_users_30_sum(dateUserReportStatList.stream().mapToLong(m -> m.getOld_users()).sum());
                responseData.setOld_users_30_avg(Float.valueOf(decimalFormat.format(dateUserReportStatList.stream().mapToLong(m -> m.getOld_users()).average().getAsDouble())));

                if (responseData.getTotal_active_users_30() > 0) {
                    responseData.setNew_users_percent_30_avg(Float.valueOf(decimalFormat.format(responseData.getTotal_new_users_30() * 100f / responseData.getTotal_active_users_30())));
                }

                long totalLeaveTimes = dateUserReportStatList.stream().mapToLong(m -> m.getLeave_times()).sum();
                if (totalLeaveTimes > 0) {
                    Double duration_per_time = totalDuration / totalLeaveTimes;
                    responseData.setDuration_per_time_30_avg(Float.valueOf(decimalFormat.format(duration_per_time.floatValue())));
                }

                responseData.setTimes(times);
                responseData.setActive_users_30(active_users_30);
                responseData.setDuration_per_user_30(duration_per_user_30);
                responseData.setNew_pv_30(new_pv_30);
                responseData.setDuration_per_time_30(duration_per_time_30);
                responseData.setNew_users_30(new_users_30);
                responseData.setOld_users_30(old_users_30);
                responseData.setNew_users_percent_30(new_users_percent_30);

            }
            // 7日留存
            List<UserRetentionBydate> userRetentionBydateList = clickHouseJdbcTemplate.query(getDateRetentionSql, paramMap, new BeanPropertyRowMapper<UserRetentionBydate>(UserRetentionBydate.class));

            // 次周留存
            List<UserRetentionByweek> userRetentionByweekList = clickHouseJdbcTemplate.query(getWeekRetentionSql, paramMap, new BeanPropertyRowMapper<UserRetentionByweek>(UserRetentionByweek.class));

            List<Long> retention_users_d7 = new ArrayList<>();
            List<Long> retention_users_w1 = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                Timestamp tsDate = new Timestamp(start30Time.getTime() + DateUtils.MILLIS_PER_DAY * i);
                String sDate = yMdFORMAT.get().format(tsDate);

                Optional<UserRetentionBydate> optUserRetentionBydate = userRetentionBydateList.stream().filter(f -> f.getStat_date().getTime() == tsDate.getTime()).findAny();
                if (optUserRetentionBydate.isPresent()) {
                    retention_users_d7.add(optUserRetentionBydate.get().getD7());
                    responseData.setRetention_users_d7_latest(optUserRetentionBydate.get().getD7());
                } else {
                    retention_users_d7.add(0L);
                }
                Optional<UserRetentionByweek> optUserRetentionByweek = userRetentionByweekList.stream().filter(f -> f.getStat_date().getTime() == tsDate.getTime()).findAny();
                if (optUserRetentionByweek.isPresent()) {
                    retention_users_w1.add(optUserRetentionByweek.get().getW1());
                    responseData.setRetention_users_w1_latest(optUserRetentionByweek.get().getW1());
                } else {
                    retention_users_w1.add(0L);
                }
            }

            responseData.setRetention_users_d7(retention_users_d7);
            responseData.setRetention_users_d7_sum(userRetentionBydateList.stream().mapToLong(m -> m.getD7()).sum());
            if (userRetentionBydateList.size() > 0) {
                Float avg = Float.valueOf(decimalFormat.format(userRetentionBydateList.stream().mapToLong(m -> m.getD7()).average().getAsDouble()));
                responseData.setRetention_users_d7_avg(avg);
            }

            responseData.setRetention_users_w1(retention_users_w1);
            responseData.setRetention_users_w1_sum(userRetentionByweekList.stream().mapToLong(m -> m.getW1()).sum());
            if (userRetentionByweekList.size() > 0) {
                Float avg = Float.valueOf(decimalFormat.format(userRetentionByweekList.stream().mapToLong(m -> m.getW1()).average().getAsDouble()));
                responseData.setRetention_users_w1_avg(avg);
            }
            response.setData(responseData);

        } catch (Exception ex) {
            logger.error("getRealStat error,", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public LifeCycleStatResponse getLifeCycleStat(LifeCycleStatRequest lifeCycleStatRequest) {
        LifeCycleStatResponse response = new LifeCycleStatResponse();
        try {
            long now = System.currentTimeMillis();
            Timestamp endTime = new Timestamp(now);
            endTime = Timestamp.valueOf(this.yMdFORMAT.get().format(endTime) + " 00:00:00");

            Timestamp startTime = new Timestamp(endTime.getTime() - lifeCycleStatRequest.getPast() * DateUtils.MILLIS_PER_DAY);

            String getDateSql = "select * from sensor_user_life_report_bydate where stat_date>=:start_date and stat_date<=:end_date";

            String where = "";

            String today = yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis()));
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            paramMap.addValue("start_date", yMdFORMAT.get().format(startTime));
            paramMap.addValue("end_date", yMdFORMAT.get().format(endTime));
            if (StringUtils.isNotBlank(lifeCycleStatRequest.getChannel())) {
                where += " and lib=:channel";
                paramMap.addValue("channel", lifeCycleStatRequest.getChannel());
            }
            String projectName = filterProject(lifeCycleStatRequest.getProjectName());
            if (StringUtils.isNotBlank(projectName)) {
                where += " and project_name=:project ";
                paramMap.addValue("project", projectName);
            }
            if (StringUtils.isNotBlank(lifeCycleStatRequest.getDownloadChannel())) {
                where += " and download_channel=:download_channel";
                String downloadChannel = lifeCycleStatRequest.getDownloadChannel();
                if ("无".equalsIgnoreCase(downloadChannel)) {
                    downloadChannel = "";
                }

                paramMap.addValue("download_channel", downloadChannel);
            }
            if (StringUtils.isNotBlank(lifeCycleStatRequest.getAppVersion())) {
                where += " and app_version=:app_version";
                String version = lifeCycleStatRequest.getAppVersion();
                if ("无".equalsIgnoreCase(version)) {
                    version = "";
                }
                paramMap.addValue("app_version", version);
            }

            if (StringUtils.isNotBlank(where)) {
                getDateSql += where + " order by stat_date";
            }

            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            List<UserLifeReportBydate> dateUserLifeReportStatList = clickHouseJdbcTemplate.query(getDateSql, paramMap, new BeanPropertyRowMapper<UserLifeReportBydate>(UserLifeReportBydate.class));
            LifeCycleStatResponseData responseData = new LifeCycleStatResponseData();
            responseData.setEndTime(endTime);
            responseData.setStartTime(startTime);

            List<String> times = new ArrayList<>();

            List<Long> new_users_list = new ArrayList<>();
            List<Long> continuous_active_users_list = new ArrayList<>();
            List<Long> revisit_users_list = new ArrayList<>();
            List<Long> silent_users_list = new ArrayList<>();
            List<Long> churn_users_list = new ArrayList<>();
            List<Float> new_and_revisite_churn_percent_list = new ArrayList<>();


            if (dateUserLifeReportStatList.size() > 0) {
                UserLifeReportBydate latestLife = dateUserLifeReportStatList.get(dateUserLifeReportStatList.size() - 1);
                responseData.setNew_users(latestLife.getNew_users());
                responseData.setContinuous_active_users(latestLife.getContinuous_active_users());
                responseData.setRevisit_users(latestLife.getRevisit_users());
                responseData.setSilent_users(latestLife.getSilent_users());
                responseData.setChurn_users(latestLife.getChurn_users());
                responseData.setNew_and_revisite_churn_percent(0f);
                if (latestLife.getChurn_users() > 0) {
                    Float percent = Float.valueOf(decimalFormat.format((latestLife.getNew_users() + latestLife.getRevisit_users()) * 100f / latestLife.getChurn_users()));
                    responseData.setNew_and_revisite_churn_percent(percent);
                }

                responseData.setNew_users_raise_percent(0f);
                responseData.setContinuous_active_users_raise_percent(0f);
                responseData.setRevisit_users_raise_percent(0f);
                responseData.setSilent_users_raise_percent(0f);
                responseData.setChurn_users_raise_percent(0f);
                responseData.setNew_and_revisite_churn_percent_raise_percent(0f);
                if (dateUserLifeReportStatList.size() > 1) {
                    UserLifeReportBydate beforelatestLife = dateUserLifeReportStatList.get(dateUserLifeReportStatList.size() - 2);
                    if (beforelatestLife.getNew_users() > 0) {
                        Float percent = Float.valueOf(decimalFormat.format(latestLife.getNew_users() * 100f / beforelatestLife.getNew_users() - 100));
                        responseData.setNew_users_raise_percent(percent);
                    }
                    if (beforelatestLife.getContinuous_active_users() > 0) {
                        Float percent = Float.valueOf(decimalFormat.format(latestLife.getContinuous_active_users() * 100f / beforelatestLife.getContinuous_active_users() - 100));
                        responseData.setContinuous_active_users_raise_percent(percent);
                    }
                    if (beforelatestLife.getRevisit_users() > 0) {
                        Float percent = Float.valueOf(decimalFormat.format(latestLife.getRevisit_users() * 100f / beforelatestLife.getRevisit_users() - 100));
                        responseData.setRevisit_users_raise_percent(percent);
                    }
                    if (beforelatestLife.getSilent_users() > 0) {
                        Float percent = Float.valueOf(decimalFormat.format(latestLife.getSilent_users() * 100f / beforelatestLife.getSilent_users() - 100));
                        responseData.setSilent_users_raise_percent(percent);
                    }
                    if (beforelatestLife.getChurn_users() > 0) {
                        Float percent = Float.valueOf(decimalFormat.format(latestLife.getChurn_users() * 100f / beforelatestLife.getChurn_users() - 100));
                        responseData.setChurn_users_raise_percent(percent);
                    }

                    if (beforelatestLife.getChurn_users() > 0) {
                        Float beforeLatestpercent = (beforelatestLife.getNew_users() + beforelatestLife.getRevisit_users()) * 100f / beforelatestLife.getChurn_users();
                        if (beforeLatestpercent > 0) {
                            Float beforeLatestpercentRaisePercent = Float.valueOf(decimalFormat.format(responseData.getNew_and_revisite_churn_percent() * 100 / beforeLatestpercent));
                            responseData.setNew_and_revisite_churn_percent_raise_percent(beforeLatestpercentRaisePercent);
                        }
                    }
                }
            }

            for (int i = 0; i < lifeCycleStatRequest.getPast(); i++) {
                Timestamp tsDate = new Timestamp(startTime.getTime() + DateUtils.MILLIS_PER_DAY * i);
                String sDate = yMdFORMAT.get().format(tsDate);
                times.add(sDate);
                Optional<UserLifeReportBydate> optUserLifeReportBydate = dateUserLifeReportStatList.stream().filter(f -> f.getStat_date().getTime() == tsDate.getTime()).findAny();
                if (optUserLifeReportBydate.isPresent()) {
                    UserLifeReportBydate userLifeReport = optUserLifeReportBydate.get();
                    responseData.setUser_life_bydate_updatetime(userLifeReport.getUpdate_time());
                    new_users_list.add(userLifeReport.getNew_users());
                    continuous_active_users_list.add(userLifeReport.getContinuous_active_users());
                    revisit_users_list.add(userLifeReport.getRevisit_users());
                    silent_users_list.add(userLifeReport.getSilent_users());
                    churn_users_list.add(userLifeReport.getChurn_users());
                    if (userLifeReport.getChurn_users() > 0) {
                        Float percent = Float.valueOf(decimalFormat.format((userLifeReport.getNew_users() + userLifeReport.getRevisit_users()) * 1.0f / userLifeReport.getChurn_users()));
                        new_and_revisite_churn_percent_list.add(percent);
                    } else {
                        new_and_revisite_churn_percent_list.add(0f);
                    }

                } else {
                    new_users_list.add(0L);
                    continuous_active_users_list.add(0L);
                    revisit_users_list.add(0L);
                    silent_users_list.add(0L);
                    churn_users_list.add(0L);
                    new_and_revisite_churn_percent_list.add(0f);
                }
            }
            responseData.setTimes(times);
            responseData.setNew_users_list(new_users_list);
            responseData.setContinuous_active_users_list(continuous_active_users_list);
            responseData.setRevisit_users_list(revisit_users_list);
            responseData.setSilent_users_list(silent_users_list);
            responseData.setChurn_users_list(churn_users_list);
            responseData.setNew_and_revisite_churn_percent_list(new_and_revisite_churn_percent_list);
            response.setData(responseData);

        } catch (Exception ex) {
            logger.error("getRealStat error,", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public AreaStatResponse getAreaStat(AreaStatRequest areaStatRequest) {
        AreaStatResponse response = new AreaStatResponse();
        try {
            long now = System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY;
            Timestamp endTime = new Timestamp(now);
            endTime = Timestamp.valueOf(this.yMdFORMAT.get().format(endTime) + " 00:00:00");

            Timestamp startTime = new Timestamp(endTime.getTime() - (areaStatRequest.getPast() - 1) * DateUtils.MILLIS_PER_DAY);

            String getAreaSql = "select stat_date,province,sum(new_users) new_users,sum(active_users) active_users,max(update_time) update_time from sensor_user_report_byarea where stat_date>=:start_date and stat_date<=:end_date and country='中国' and province is not null and province <>'' ";

            String where = "";

            String today = yMdFORMAT.get().format(new Timestamp(System.currentTimeMillis()));
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            paramMap.addValue("start_date", yMdFORMAT.get().format(startTime));
            paramMap.addValue("end_date", yMdFORMAT.get().format(endTime));
            if (StringUtils.isNotBlank(areaStatRequest.getChannel())) {
                where += " and lib=:channel";
                paramMap.addValue("channel", areaStatRequest.getChannel());
            }
            String projectName = filterProject(areaStatRequest.getProjectName());
            if (StringUtils.isNotBlank(projectName)) {
                where += " and project_name=:project ";
                paramMap.addValue("project", projectName);
            }
            if (StringUtils.isNotBlank(areaStatRequest.getDownloadChannel())) {
                where += " and download_channel=:download_channel";
                String downloadChannel = areaStatRequest.getDownloadChannel();
                if ("无".equalsIgnoreCase(downloadChannel)) {
                    downloadChannel = "";
                }

                paramMap.addValue("download_channel", downloadChannel);
            }
            if (StringUtils.isNotBlank(areaStatRequest.getAppVersion())) {
                where += " and app_version=:app_version";
                String version = areaStatRequest.getAppVersion();
                if ("无".equalsIgnoreCase(version)) {
                    version = "";
                }
                paramMap.addValue("app_version", version);
            }

            if (StringUtils.isNotBlank(where)) {
                getAreaSql += where + " group by stat_date,province";
            }

            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            List<UserReportbyarea> UserReportbyareaStatList = clickHouseJdbcTemplate.query(getAreaSql, paramMap, new BeanPropertyRowMapper<UserReportbyarea>(UserReportbyarea.class));
            AreaStatResponseData responseData = new AreaStatResponseData();
            responseData.setEndTime(endTime);
            responseData.setStartTime(startTime);

            List<String> times = new ArrayList<>();

            if (UserReportbyareaStatList.size() > 0) {
                UserReportbyarea latestLife = UserReportbyareaStatList.get(UserReportbyareaStatList.size() - 1);
                responseData.setArea_bydate_updatetime(latestLife.getUpdate_time());
            }

            List<String> areaList = UserReportbyareaStatList.stream().map(m -> m.getProvince()).distinct().sorted().collect(Collectors.toList());

            // 活跃用户按省份
            responseData.setActive_users_areas(areaList);
            Map<String, Long> activeUsersSum = UserReportbyareaStatList.stream().collect(Collectors.groupingBy(UserReportbyarea::getProvince,
                    Collectors.summingLong(UserReportbyarea::getActive_users)));

            List<Long> activeUserByProvince = new ArrayList<>();
            for (String area : areaList) {
                if (activeUsersSum.containsKey(area)) {
                    activeUserByProvince.add(activeUsersSum.get(area));
                } else {
                    activeUserByProvince.add(0L);
                }
            }
            responseData.setActive_users_areas_list(activeUserByProvince);

            // 新增用户按省份
            responseData.setNew_users_areas(areaList);
            Map<String, Long> newUsersSum = UserReportbyareaStatList.stream().collect(Collectors.groupingBy(UserReportbyarea::getProvince,
                    Collectors.summingLong(UserReportbyarea::getNew_users)));

            List<Long> newUserByProvince = new ArrayList<>();
            for (String area : areaList) {
                if (newUsersSum.containsKey(area)) {
                    newUserByProvince.add(newUsersSum.get(area));
                } else {
                    newUserByProvince.add(0L);
                }
            }
            responseData.setNew_users_areas_list(newUserByProvince);

            for (int i = 0; i < areaStatRequest.getPast(); i++) {
                Timestamp tsDate = new Timestamp(startTime.getTime() + DateUtils.MILLIS_PER_DAY * i);
                String sDate = yMdFORMAT.get().format(tsDate);
                times.add(sDate);
            }

            List<List<Long>> newUserByAreaAndTimeList = new ArrayList<>();
            for (String newUserArea : responseData.getNew_users_areas()) {
                List<Long> newUserByAreaList = new ArrayList<>();
                for (int i = 0; i < areaStatRequest.getPast(); i++) {
                    Timestamp tsDate = new Timestamp(startTime.getTime() + DateUtils.MILLIS_PER_DAY * i);
                    Optional<UserReportbyarea> optUserReportbyarea = UserReportbyareaStatList.stream().filter(f -> f.getStat_date().getTime() == tsDate.getTime() && f.getProvince().equalsIgnoreCase(newUserArea)).findAny();
                    if (optUserReportbyarea.isPresent()) {
                        newUserByAreaList.add(optUserReportbyarea.get().getNew_users());
                    } else {
                        newUserByAreaList.add(0L);
                    }
                }
                newUserByAreaAndTimeList.add(newUserByAreaList);
            }

            List<List<Long>> activeUserByAreaAndTimeList = new ArrayList<>();
            for (String activeUserArea : responseData.getActive_users_areas()) {
                List<Long> activeUserByAreaList = new ArrayList<>();
                for (int i = 0; i < areaStatRequest.getPast(); i++) {
                    Timestamp tsDate = new Timestamp(startTime.getTime() + DateUtils.MILLIS_PER_DAY * i);
                    Optional<UserReportbyarea> optUserReportbyarea = UserReportbyareaStatList.stream().filter(f -> f.getStat_date().getTime() == tsDate.getTime() && f.getProvince().equalsIgnoreCase(activeUserArea)).findAny();
                    if (optUserReportbyarea.isPresent()) {
                        activeUserByAreaList.add(optUserReportbyarea.get().getActive_users());
                    } else {
                        activeUserByAreaList.add(0L);
                    }
                }
                activeUserByAreaAndTimeList.add(activeUserByAreaList);
            }
            responseData.setNew_users_areas_bytime_list(newUserByAreaAndTimeList);
            responseData.setActive_users_areas_bytime_list(activeUserByAreaAndTimeList);

            responseData.setTimes(times);
            response.setData(responseData);

        } catch (Exception ex) {
            logger.error("getRealStat error,", ex);
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public QueryCityNewUsersResponse getCityNewUsersList(QueryCityNewUsersRequest queryCityNewUsersRequest) {

        QueryCityNewUsersResponse response = new QueryCityNewUsersResponse();
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();

            String getListSql = "select city,sum(new_users) as amount from sensor_user_report_byarea ";
            String getCountSql = "select count(distinct(city)) from sensor_user_report_byarea";
            String order = " group by city order by sum(new_users) desc";
            String where = " where new_users >0 and country='中国' and city is not null and city <>''";

            long now = System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY;
            Timestamp endTime = new Timestamp(now);
            endTime = Timestamp.valueOf(this.yMdFORMAT.get().format(endTime) + " 00:00:00");

            Timestamp startTime = new Timestamp(endTime.getTime() - (queryCityNewUsersRequest.getPast() - 1) * DateUtils.MILLIS_PER_DAY);

            if (StringUtils.isNotBlank(queryCityNewUsersRequest.getChannel())) {
                where += " and lib=:channel";
                paramMap.addValue("channel", queryCityNewUsersRequest.getChannel());
            }

            where += " and stat_date>=:starttime ";
            paramMap.addValue("starttime", this.yMdFORMAT.get().format(startTime));


            where += " and stat_date<:endtime ";
            paramMap.addValue("endtime", this.yMdFORMAT.get().format(endTime));

            String projectName = filterProject(queryCityNewUsersRequest.getProjectName());
            if (StringUtils.isNotBlank(projectName)) {
                where += " and project_name=:project ";
                paramMap.addValue("project", projectName);
            }
            if (StringUtils.isNotBlank(queryCityNewUsersRequest.getDownloadChannel())) {
                where += " and download_channel=:download_channel";
                String downloadChannel = queryCityNewUsersRequest.getDownloadChannel();
                if ("无".equalsIgnoreCase(downloadChannel)) {
                    downloadChannel = "";
                }

                paramMap.addValue("download_channel", downloadChannel);
            }
            if (StringUtils.isNotBlank(queryCityNewUsersRequest.getAppVersion())) {
                where += " and app_version=:app_version";
                String version = queryCityNewUsersRequest.getAppVersion();
                if ("无".equalsIgnoreCase(version)) {
                    version = "";
                }
                paramMap.addValue("app_version", version);
            }

            if (StringUtils.isNotBlank(where)) {
                getListSql += where;
                getCountSql += where;
            }
            getListSql += order + " limit " + (queryCityNewUsersRequest.getPageNum() - 1) * queryCityNewUsersRequest.getPageSize() + "," + queryCityNewUsersRequest.getPageSize();

            Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
            List<CityUser> userStatList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<CityUser>(CityUser.class));

            QueryCityNewUsersResponseData responseData = new QueryCityNewUsersResponseData();

            responseData.setRows(userStatList);
            responseData.setTotal(total);
            response.setData(responseData);

        } catch (Exception ex) {
            logger.error("getCityNewUsersList error," + ex.getMessage());
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public QueryCityActiveUsersResponse getCityActiveUsersList(QueryCityActiveUsersRequest queryCityActiveUsersRequest) {
        QueryCityActiveUsersResponse response = new QueryCityActiveUsersResponse();
        try {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();

            String getListSql = "select city,sum(active_users) as amount from sensor_user_report_byarea ";
            String getCountSql = "select count(distinct(city)) from sensor_user_report_byarea";
            String order = " group by city order by sum(active_users) desc";
            String where = " where active_users >0 and country='中国' and city is not null and city <>''";

            long now = System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY;
            Timestamp endTime = new Timestamp(now);
            endTime = Timestamp.valueOf(this.yMdFORMAT.get().format(endTime) + " 00:00:00");

            Timestamp startTime = new Timestamp(endTime.getTime() - (queryCityActiveUsersRequest.getPast() - 1) * DateUtils.MILLIS_PER_DAY);

            if (StringUtils.isNotBlank(queryCityActiveUsersRequest.getChannel())) {
                where += " and lib=:channel";
                paramMap.addValue("channel", queryCityActiveUsersRequest.getChannel());
            }

            where += " and stat_date>=:starttime ";
            paramMap.addValue("starttime", this.yMdFORMAT.get().format(startTime));


            where += " and stat_date<:endtime ";
            paramMap.addValue("endtime", this.yMdFORMAT.get().format(endTime));

            String projectName = filterProject(queryCityActiveUsersRequest.getProjectName());
            if (StringUtils.isNotBlank(projectName)) {
                where += " and project_name=:project ";
                paramMap.addValue("project", projectName);
            }
            if (StringUtils.isNotBlank(queryCityActiveUsersRequest.getDownloadChannel())) {
                where += " and download_channel=:download_channel";
                String downloadChannel = queryCityActiveUsersRequest.getDownloadChannel();
                if ("无".equalsIgnoreCase(downloadChannel)) {
                    downloadChannel = "";
                }

                paramMap.addValue("download_channel", downloadChannel);
            }
            if (StringUtils.isNotBlank(queryCityActiveUsersRequest.getAppVersion())) {
                where += " and app_version=:app_version";
                String version = queryCityActiveUsersRequest.getAppVersion();
                if ("无".equalsIgnoreCase(version)) {
                    version = "";
                }
                paramMap.addValue("app_version", version);
            }

            if (StringUtils.isNotBlank(where)) {
                getListSql += where;
                getCountSql += where;
            }
            getListSql += order + " limit " + (queryCityActiveUsersRequest.getPageNum() - 1) * queryCityActiveUsersRequest.getPageSize() + "," + queryCityActiveUsersRequest.getPageSize();

            Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
            List<CityUser> userStatList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<CityUser>(CityUser.class));

            QueryCityActiveUsersResponseData responseData = new QueryCityActiveUsersResponseData();

            responseData.setRows(userStatList);
            responseData.setTotal(total);
            response.setData(responseData);

        } catch (Exception ex) {
            logger.error("getCityActiveUsersList error," + ex.getMessage());
            response.setCode(500);
            response.setMsg("操作失败");
        }
        return response;
    }

    @Override
    public GetAppVersionResponse getAppVersion() {
        GetAppVersionResponse response = new GetAppVersionResponse();
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        List<AppVersionData> versionDataList = mysqlJdbcTemplate.query("select distinct version,label,ordernum from  tbl_stat_appversion  order by ordernum,version", paramMap, new BeanPropertyRowMapper<AppVersionData>(AppVersionData.class));
        response.setData(versionDataList);
        return response;
    }

    @Override
    public GetDownloadChannelResponse getDownloadChannel() {
        GetDownloadChannelResponse response = new GetDownloadChannelResponse();
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        List<DownloadChannelData> downloadChannelList = mysqlJdbcTemplate.query("select distinct channel,label,ordernum from  tbl_stat_downloadchannel  order by ordernum,channel", paramMap, new BeanPropertyRowMapper<DownloadChannelData>(DownloadChannelData.class));
        response.setData(downloadChannelList);
        return response;
    }

    @Override
    public GetAppResponse getApp(GetAppRequest getAppRequest) {
        GetAppResponse response = new GetAppResponse();
        response.setData(getAppByUser(getAppRequest.getType()));
        return response;
    }

    private List<AppData> getAppByUser(String type) {
        List<AppData> appList = new ArrayList<>();
        LoginUser loginUser = SecurityUtils.getLoginUser();

        List<SysRole> roleList = loginUser.getUser().getRoles();
        List<String> roleKeyList = roleList.stream().map(SysRole::getRoleKey).collect(Collectors.toList());

        if (roleKeyList.contains("normalanalysis")) {
            MapSqlParameterSource paramMap = new MapSqlParameterSource();
            paramMap.addValue("id", loginUser.getDeptId());
            List<String> deptList = mysqlJdbcTemplate.queryForList("select dept_name from sys_dept where dept_id=:id", paramMap, String.class);
            if (deptList.size() > 0) {
                String dept = deptList.get(0);
                String[] deptPair = dept.split("\\(", -1);
                AppData appData = new AppData();
                appData.setLabel(deptPair[0]);
                appData.setValue(deptPair[0]);
                if (deptPair.length > 1) {
                    appData.setValue(deptPair[1].replaceAll("\\)", ""));
                }
                appList.add(appData);
            }

        } else {
            if ("user".equalsIgnoreCase(type)) {
                AppData appData = new AppData();
                appData.setValue("");
                appData.setLabel("所有");
                appList.add(appData);
            }
            AppData appData1 = new AppData();
            appData1.setValue("gpapp");
            appData1.setLabel("国拍App");
            appList.add(appData1);

            AppData appData2 = new AppData();
            appData2.setValue("gpzf");
            appData2.setLabel("国拍租房");
            appList.add(appData2);

            AppData appData3 = new AppData();
            appData3.setValue("live");
            appData3.setLabel("在线拍");
            appList.add(appData3);
        }
        return appList;
    }

    private String filterProject(String projectReq) {
        String project = "hasnoproject";
        List<AppData> appDataList = getAppByUser("");
        if (appDataList.size() == 0) {
            project = "hasnoproject";
        } else if (appDataList.size() == 1) {
            project = appDataList.get(0).getValue();
        } else {
            if (StringUtils.isNotBlank(projectReq)) {
                Optional<AppData> optionalAppData = appDataList.stream().filter(f -> f.getValue().equalsIgnoreCase(projectReq)).findAny();
                if (optionalAppData.isPresent()) {
                    project = projectReq;
                } else {
                    project = appDataList.get(0).getValue();
                }
            } else {
                project = "";
            }
        }
        return project;
    }
}
