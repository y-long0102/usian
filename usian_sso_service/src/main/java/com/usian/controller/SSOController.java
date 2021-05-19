package com.usian.controller;

import com.usian.pojo.TbUser;
import com.usian.service.SSOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("service/sso")
public class SSOController {

    @Autowired
    private SSOService ssoService;

    @RequestMapping("checkUserInfo")
    public Boolean checkUserInfo(String checkValue, Integer checkFlag){
        // 校验通过就是true， 不通过就是false
        return ssoService.checkUserInfo(checkValue, checkFlag);
    }

    @RequestMapping("userRegister")
    public Integer userRegister(@RequestBody TbUser tbUser){
        return ssoService.userRegister(tbUser);
    }

    @RequestMapping("userLogin")
    public Map<String, Object> userLogin(@RequestBody TbUser user){
        return ssoService.userLogin(user);
    }

    @RequestMapping("getUserByToken")
    public TbUser getUserByToken(@RequestParam String token){
        return ssoService.getUserByToken(token);
    }

    @RequestMapping("getUserByUserId")
    public TbUser getUserByUserId(String userId){
        return ssoService.getUserByUserId(userId);
    }
}
