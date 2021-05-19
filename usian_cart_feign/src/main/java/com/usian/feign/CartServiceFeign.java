package com.usian.feign;

import com.usian.pojo.TbItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("usian-cart-service")
public interface CartServiceFeign {

    @RequestMapping("service/cart/getCartFromRedis")
    Map<String, TbItem> getCartFromRedis(@RequestParam String userId);

    @RequestMapping("service/cart/addClientRedis")
    Boolean addClientRedis(@RequestParam String userId, @RequestBody Map<String, TbItem> cartFromCookie);

    @RequestMapping("service/cart/deleteCartFromRedis")
    Boolean deleteCartFromRedis(@RequestParam String userId,@RequestParam Long itemId);
}
