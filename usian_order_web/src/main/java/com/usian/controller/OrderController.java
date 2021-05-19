package com.usian.controller;

import com.usian.feign.CartServiceFeign;
import com.usian.feign.OrderServiceFeign;
import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbOrder;
import com.usian.pojo.TbOrderShipping;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("frontend/order")
public class OrderController {

    @Autowired
    private OrderServiceFeign orderServiceFeign;

    @Autowired
    private CartServiceFeign cartServiceFeign;

    @RequestMapping("goSettlement")
    public Result goSettlement(String[] ids, String userId){
        // 判断userId是否为空，如果为空说明未登录
//        if(StringUtils.isEmpty(userId)){
//            return Result.error("当前未登录");
//        }

        // 从redis中取出购物车
        Map<String, TbItem> cartFromRedis = cartServiceFeign.getCartFromRedis(userId);
        if(ids != null && ids.length > 0){
            List<TbItem> tbItems = new ArrayList<>();
            for (String id : ids) {
                TbItem tbItem = cartFromRedis.get(id);
                tbItems.add(tbItem);
            }
            if(tbItems.size() > 0){
                return Result.ok(tbItems);
            }
        }

        return Result.error("跳转订单页面失败");
    }

    @RequestMapping("insertOrder")
    public Result insertOrder(String orderItem, TbOrder tbOrder , TbOrderShipping
            tbOrderShipping){
        //因为一个request中只包含一个request body. 所以feign不支持多个@RequestBody。
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderItem(orderItem);
        orderInfo.setTbOrder(tbOrder);
        orderInfo.setTbOrderShipping(tbOrderShipping);
        Long orderId = orderServiceFeign.insertOrder(orderInfo);
        if(orderId != null){
            return Result.ok(orderId);
        }
        return Result.error("提交订单失败");
    }
}
