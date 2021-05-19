package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("frontend/detail")
public class ItemDetailController {

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    @RequestMapping("selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem item = itemServiceFeign.selectItemInfo(itemId);
        if(item != null){
            return Result.ok(item);
        }
        return Result.error("啥也没查出来。。。");
    }

    @RequestMapping("selectItemDescByItemId")
    public Result selectItemDescByItemId(Long itemId){
        TbItemDesc tbItemDesc = itemServiceFeign.selectItemDescByItemId(itemId);
        if(tbItemDesc != null){
            return Result.ok(tbItemDesc);
        }
        return Result.error("啥也没查出来。。。");
    }

    @RequestMapping("selectTbItemParamItemByItemId")
    public Result selectTbItemParamItemByItemId(Long itemId){
        TbItemParamItem tbItemParamItem = itemServiceFeign.selectTbItemParamItemByItemId(itemId);
        if(tbItemParamItem != null){
            return Result.ok(tbItemParamItem);
        }
        return Result.error("啥也没查出来。。。");
    }
}
