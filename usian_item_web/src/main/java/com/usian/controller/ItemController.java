package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("frontend/item")
public class ItemController {

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    @RequestMapping("selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);
        if(tbItem != null){
            return Result.ok(tbItem);
        }
        return Result.error("兄弟，你请求有问题，检查一下肾。。。");
    }
}
