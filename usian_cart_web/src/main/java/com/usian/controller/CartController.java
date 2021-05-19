package com.usian.controller;

import com.usian.feign.CartServiceFeign;
import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.utils.CookieUtils;
import com.usian.utils.JsonUtils;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("frontend/cart")
public class CartController {

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    @Autowired
    private CartServiceFeign cartServiceFeign;

    @Value("${CART_COOKIE_KEY}")
    private String CART_COOKIE_KEY;

    @Value("${CART_COOKIE_EXPIRE}")
    private Integer CART_COOKIE_EXPIRE;

    @RequestMapping("addItem")
    public Result addItem(String userId, Long itemId, @RequestParam(defaultValue = "1") Integer num, HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isEmpty(userId)){
            // 如果userId为空说明未登录
            // 通过商品ID获取商品信息
//            TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);
//            // 设置商品在购物车中的数据量
//            tbItem.setNum(num);
//            // 获取购物车，如果有购物车则直接用， 如果没有购物车就创建一个购物车
//            Map<String, TbItem> cartFromCookie = this.getCartFromCookie(request);
//            // 将商品添加购购物车， /
//            cartFromCookie.put(itemId.toString(), tbItem);
            // 将修改后的cookie返回给浏览器客户端
            // 将修改后的cookie返回给浏览器客户端
            // 将修改后的cookie返回给浏览器客户端
            Map<String, TbItem> cartFromCookie = this.getCartFromCookie(request);
            this.addItemToCart(cartFromCookie, itemId, num);
            this.addClientCookie(request, response, cartFromCookie);
            return Result.ok();
        }else{
            // 如果userId不为空说明已登录
            Map<String, TbItem> cartFromCookie = cartServiceFeign.getCartFromRedis(userId);
            this.addItemToCart(cartFromCookie, itemId, num);
            Boolean b = cartServiceFeign.addClientRedis(userId, cartFromCookie);
            if (b){
                return Result.ok();
            }
        }
        return Result.error("添加购物车失败");
    }

    @RequestMapping("showCart")
    public Result showCart(String userId, HttpServletRequest request){
        // 判断当前用户是否登录
        if(StringUtils.isEmpty(userId)){
            // userId为空说明未登录
            // 从cookie中获取购物车数据
            Map<String, TbItem> cartFromCookie = this.getCartFromCookie(request);
            Collection<TbItem> values = cartFromCookie.values();
            return Result.ok(values);
        }else {
            // 不为空说明登录
            Map<String, TbItem> cartFromCookie = cartServiceFeign.getCartFromRedis(userId);
            Collection<TbItem> values = cartFromCookie.values();
            return Result.ok(values);
        }
    }

    @RequestMapping("updateItemNum")
    public Result updateItemNum(String userId, Long itemId, @RequestParam(defaultValue = "1") Integer num, HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isEmpty(userId)){
            Map<String, TbItem> cartFromCookie = this.getCartFromCookie(request);
//            this.addItemToCart(cartFromCookie, itemId, num);
            //从购物车中取商品
            TbItem tbItem = cartFromCookie.get(itemId.toString());
            tbItem.setNum(num);
            this.addClientCookie(request, response, cartFromCookie);
            return Result.ok();
        }else{
            // 如果userId不为空说明已登录
            Map<String, TbItem> cartFromCookie = cartServiceFeign.getCartFromRedis(userId);
//            this.addItemToCart(cartFromCookie, itemId, num);
            //从购物车中取商品
            TbItem tbItem = cartFromCookie.get(itemId.toString());
            tbItem.setNum(num);
            Boolean b = cartServiceFeign.addClientRedis(userId, cartFromCookie);
            if(b){
                return Result.ok();
            }
        }
        return Result.error("添加购物车失败");
    }


    @RequestMapping("deleteItemFromCart")
    public Result deleteItemFromCart(String userId, Long itemId, HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isEmpty(userId)){
            Map<String, TbItem> cartFromCookie = this.getCartFromCookie(request);
//            this.addItemToCart(cartFromCookie, itemId, num);
            //从购物车中取删除商品
            cartFromCookie.remove(itemId.toString());
            this.addClientCookie(request, response, cartFromCookie);
            return Result.ok();
        }else{
            // 如果userId不为空说明已登录
            Boolean b = cartServiceFeign.deleteCartFromRedis(userId, itemId);
            if(b){
                return Result.ok();
            }
        }
        return Result.error("删除失败");
    }


    /**
     * 把购车商品列表写入cookie
     * @param request
     * @param response
     * @param cart
     */
    private void addClientCookie(HttpServletRequest request,HttpServletResponse response,
                                 Map<String,TbItem> cart){
        String cartJson = JsonUtils.objectToJson(cart);
        CookieUtils.setCookie(request, response, this.CART_COOKIE_KEY, cartJson, CART_COOKIE_EXPIRE,true);
    }

    /**
     * 将商品添加到购物车中
     * @param cart
     * @param itemId
     * @param num
     */
    private void addItemToCart(Map<String, TbItem> cart, Long itemId,Integer num) {

        //从购物车中取商品
        TbItem tbItem = cart.get(itemId.toString());

        if(tbItem != null){
            //商品列表中存在该商品，商品数量相加。
            tbItem.setNum(tbItem.getNum() + num);
        }else{
            //商品列表中不存在该商品，根据商品id查询商品信息并添加到购车列表
            tbItem = itemServiceFeign.selectItemInfo(itemId);
            tbItem.setNum(num);
        }
        cart.put(itemId.toString(),tbItem);
    }

    /**
     * 获取购物车
     * @param request
     * @return
     */
    private Map<String, TbItem> getCartFromCookie(HttpServletRequest request) {
        String cartJson = CookieUtils.getCookieValue(request, this.CART_COOKIE_KEY, true);
        if (!StringUtils.isEmpty(cartJson)) {
            //购物车已存在
            Map<String, TbItem> map = JsonUtils.jsonToMap(cartJson, TbItem.class);
            return map;
        }
        //购物车不存在
        return new HashMap<String, TbItem>();
    }

}
