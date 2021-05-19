package com.usian.intercept;

import com.usian.feign.SSOFeignService;
import com.usian.pojo.TbUser;
import com.usian.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginIntercept implements HandlerInterceptor {

    @Autowired
    private SSOFeignService ssoFeignService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取token
//        String token = CookieUtils.getCookieValue(request, "token", true);
        // 从request中获取token
        String token = request.getParameter("token");
        String userId = request.getParameter("userId");
        if(StringUtils.isEmpty(token) && StringUtils.isEmpty(userId)){
            return false;
        }
        TbUser tbUser = null;
        if(token == null){
            tbUser = ssoFeignService.getUserByUserId(userId);
        }else{
            tbUser = ssoFeignService.getUserByToken(token);
        }
        // 从redis中获取token是否存在


        // 判断通过token获取的user是否为空，如果为空则说明未登录，不为空说明登录
        if(tbUser == null) {
            tbUser = ssoFeignService.getUserByUserId(userId);
            return false;
        }
        return true;
    }
}
