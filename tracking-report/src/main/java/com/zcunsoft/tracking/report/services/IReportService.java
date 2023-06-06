package com.zcunsoft.tracking.report.services;

import com.zcunsoft.tracking.report.model.*;

import java.util.List;

public interface IReportService {
    QueryUserStatResponse getUserStatlist(QueryUserStatRequest queryUserStatRequest);

    QueryVisitResponse getVisitlist(QueryVisitRequest queryVisitRequest);

    RealStatResponse getRealStat(RealStatRequest realStatRequest);

    EntireStatResponse getEntireStat(EntireStatRequest entireStatRequest);

    LifeCycleStatResponse getLifeCycleStat(LifeCycleStatRequest lifeCycleStatRequest);


    AreaStatResponse getAreaStat(AreaStatRequest areaStatRequest);

    QueryCityNewUsersResponse getCityNewUsersList(QueryCityNewUsersRequest queryCityNewUsersRequest);

    QueryCityActiveUsersResponse getCityActiveUsersList(QueryCityActiveUsersRequest queryCityActiveUsersRequest);

    GetAppVersionResponse getAppVersion();

    GetDownloadChannelResponse getDownloadChannel();

    GetAppResponse getApp(GetAppRequest getAppRequest);

    QuerySpecificUserListResponse getUserStat(QuerySpecificUserStatRequest querySpecificUserStatRequest);

    QueryUserDetailRespone getUserDetail(QueryUserDetailRequest queryUserDetailRequest);
}
