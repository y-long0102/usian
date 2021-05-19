package com.usian.controller;

import com.usian.feign.SSOFeignService;
import com.usian.pojo.TbUser;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.RequestWrapper;
import java.util.Map;

@RestController
@RequestMapping("frontend/sso")
public class SSOController {

    @Autowired
    private SSOFeignService ssoFeignService;

    @RequestMapping("checkUserInfo/{checkValue}/{checkFlag}")
    public Result checkUserInfo(@PathVariable("checkValue") String checkValue,@PathVariable("checkFlag") Integer checkFlag){
        // 校验通过就是true， 不通过就是false
        Boolean b = ssoFeignService.checkUserInfo(checkValue, checkFlag);
        if(b){
            return Result.ok(b);
        }
        return Result.error("校验失败");
    }

    @RequestMapping("userRegister")
    public Result userRegister(TbUser tbUser){
        Integer i = ssoFeignService.userRegister(tbUser);
        if(i == 1){
            return Result.ok();
        }
        return Result.error("注册失败");
    }

    @RequestMapping("userLogin")
    public Result userLogin(TbUser user){
        Map<String, Object> map = ssoFeignService.userLogin(user);
        if(map != null){
            return Result.ok(map);
        }
        return Result.error("登录失败");
    }

    @RequestMapping("getUserByToken/{token}")
    public Result getUserByToken(@PathVariable String token){
        TbUser tbUser = ssoFeignService.getUserByToken(token);
        if(tbUser != null){
            return Result.ok(tbUser);
        }
        return Result.error("用户未登录");
    }
}
