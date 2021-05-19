package com.usian.service;

import com.usian.config.RedisClient;
import com.usian.mapper.TbUserMapper;
import com.usian.pojo.TbUser;
import com.usian.pojo.TbUserExample;
import com.usian.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SSOService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private RedisClient redisClient;

    @Value("${USER_INFO}")
    private String USER_INFO;

    public Boolean checkUserInfo(String checkValue, Integer checkFlag) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        if(checkFlag == 1){
            criteria.andUsernameEqualTo(checkValue);
        }else if(checkFlag == 2){
            criteria.andPhoneEqualTo(checkValue);
        }else {
            return false;
        }
        List<TbUser> tbUsers = tbUserMapper.selectByExample(example);
        if(tbUsers != null && tbUsers.size() > 0){
            return false;
        }
        return true;
    }

    public Integer userRegister(TbUser tbUser) {
        Date date = new Date();
        tbUser.setCreated(date);
        tbUser.setUpdated(date);
        String pwd = MD5Utils.digest(tbUser.getPassword());
        tbUser.setPassword(pwd);
        return tbUserMapper.insert(tbUser);
    }

    public Map<String, Object> userLogin(TbUser user) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(user.getUsername());
        criteria.andPasswordEqualTo(MD5Utils.digest(user.getPassword()));
        List<TbUser> tbUsers = tbUserMapper.selectByExample(example);
        if(tbUsers == null || tbUsers.size() == 0){
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        String token = UUID.randomUUID().toString();
        map.put("token", token);
        map.put("userid", tbUsers.get(0).getId());
        map.put("username", tbUsers.get(0).getUsername());
        String key = USER_INFO + ":" + token;
        // 将登录信息放入redis中
        redisClient.set(key, tbUsers.get(0));
        redisClient.expire(key, 1800);
        return map;
    }

    public TbUser getUserByToken(String token) {
        String key = USER_INFO + ":" + token;
        return (TbUser)redisClient.get(key);
    }

    public TbUser getUserByUserId(String userId) {
        return tbUserMapper.selectByPrimaryKey(Long.valueOf(userId));
    }
}
