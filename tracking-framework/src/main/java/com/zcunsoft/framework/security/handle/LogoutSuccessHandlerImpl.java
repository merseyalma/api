package com.zcunsoft.framework.security.handle;


import com.zcunsoft.common.constant.HttpStatus;
import com.zcunsoft.common.core.domain.model.LoginUser;
import com.zcunsoft.common.utils.ServletUtils;
import com.zcunsoft.framework.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义退出处理类 返回成功
 *
 * @author ruoyi
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser!=null) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
         //   AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, "退出成功"));
        }
        ServletUtils.renderString(response, "{\"msg\":\"退出成功\",\"code\":" + HttpStatus.SUCCESS + "}");
    }
}
