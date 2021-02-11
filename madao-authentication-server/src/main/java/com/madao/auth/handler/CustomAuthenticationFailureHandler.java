package com.madao.auth.handler;

import com.madao.auth.exception.AuthException;
import com.madao.enums.StatusEnum;
import com.madao.utils.JsonData;
import com.madao.utils.JsonUtil;
import com.madao.utils.LogBack;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户登录失败时返回给前端的数据
 **/
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        LogBack.error("用户登录失败时异常----------->{}", e.getMessage(), e);
        JsonData<Void> errorResult = JsonData.failed(StatusEnum.SYSTEM_ERROR);
        if (e instanceof AuthException) {
            errorResult = JsonData.failed(StatusEnum.LOGIN_ERROR, e.getMessage());
        }
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        httpServletResponse.getWriter().write(JsonUtil.toJsonString(errorResult));
    }
}

