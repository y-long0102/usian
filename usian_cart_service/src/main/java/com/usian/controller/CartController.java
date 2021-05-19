package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.serivce.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("service/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @RequestMapping("getCartFromRedis")
    public Map<String, TbItem> getCartFromRedis(@RequestParam String userId){
        return cartService.getCartFromRedis(userId);
    }

    @RequestMapping("addClientRedis")
    public Boolean addClientRedis(@RequestParam String userId, @RequestBody Map<String, TbItem> cartFromCookie){
        return cartService.addClientRedis(userId, cartFromCookie);
    }

    @RequestMapping("deleteCartFromRedis")
    public Boolean deleteCartFromRedis(String userId, Long itemId){
        return cartService.deleteCartFromRedis(userId, itemId);
    }
}
