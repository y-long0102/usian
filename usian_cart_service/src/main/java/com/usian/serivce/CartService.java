package com.usian.serivce;

import com.usian.config.RedisClient;
import com.usian.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    private RedisClient redisClient;

    @Value("${CART_REDIS_KEY}")
    private String CART_REDIS_KEY;

    public Map<String, TbItem> getCartFromRedis(String userId) {
        Map<String, TbItem> map = (Map<String, TbItem>)redisClient.hget(CART_REDIS_KEY, userId);
        if(map == null){
            return new HashMap<>();
        }else{
            return map;
        }
    }

    public Boolean addClientRedis(String userId, Map<String, TbItem> cartFromCookie) {
        return redisClient.hset(CART_REDIS_KEY, userId, cartFromCookie);
    }

    public Boolean deleteCartFromRedis(String userId, Long itemId) {
        Map<String, TbItem> cartFromRedis = this.getCartFromRedis(userId);
        cartFromRedis.remove(itemId.toString());
        return this.addClientRedis(userId, cartFromRedis);
    }
}
