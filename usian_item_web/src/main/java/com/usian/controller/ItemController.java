package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("backend/item")
public class ItemController {

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    @RequestMapping("selectItemInfo")
    public Result selectItemInfo(Long itemId) {
        TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);
        if (tbItem != null) {
            return Result.ok(tbItem);
        }
        return Result.error("兄弟，你请求有问题，检查一下肾。。。");
    }

    @RequestMapping("selectTbItemAllByPage")
    public Result selectTbItemAllByPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer rows) {
        PageResult pageResult = itemServiceFeign.selectTbItemAllByPage(page, rows);
        if(pageResult != null){
            return Result.ok(pageResult);
        }
        return Result.error("差无结果");
    }

    @RequestMapping("insertTbItem")
    public Result insertTbItem(TbItem tbItem, String desc, String itemParams){
        Integer count = itemServiceFeign.insertTbItem(tbItem, desc, itemParams);
        if(count == 3){
            return Result.ok();
        }
        return Result.error("数据插入失败，长点心吧。。。");
    }

    @RequestMapping("preUpdateItem")
    public Result preUpdateItem(Long itemId){
        Map<String, Object> map = itemServiceFeign.preUpdateItem(itemId);
        if(map != null){
            return Result.ok(map);
        }
        return Result.error("查询不出来， 啥也不是呀。。。");
    }
}
