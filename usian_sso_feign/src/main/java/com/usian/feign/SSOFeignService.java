package com.usian.feign;

import com.usian.pojo.TbUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("usian-sso-service")
public interface SSOFeignService {

    @RequestMapping("service/sso/checkUserInfo")
    Boolean checkUserInfo(@RequestParam String checkValue,@RequestParam Integer checkFlag);

    @RequestMapping("service/sso/userRegister")
    Integer userRegister(@RequestBody TbUser tbUser);

    @RequestMapping("service/sso/userLogin")
    Map<String, Object> userLogin(@RequestBody TbUser user);

    @RequestMapping("service/sso/getUserByToken")
    TbUser getUserByToken(@RequestParam String token);

    @RequestMapping("service/sso/getUserByUserId")
    TbUser getUserByUserId(@RequestParam String userId);
}
