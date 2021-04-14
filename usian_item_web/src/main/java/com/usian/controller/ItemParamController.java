package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("backend/itemParam")
public class ItemParamController {

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    @RequestMapping("selectItemParamByItemCatId/{itemCatId}")
    public Result selectItemParamByItemCatId(@PathVariable Long itemCatId){
        TbItemParam tbItemParam = itemServiceFeign.selectItemParamByItemCatId(itemCatId);
        if(tbItemParam != null){
            return Result.ok(tbItemParam);
        }
        return Result.error("啥也查不到，啥也不是。。。");
    }

    @RequestMapping("selectItemParamAll")
    public Result selectItemParamAll(){
        PageResult page = itemServiceFeign.selectItemParamAll();
        if(page != null){
            return Result.ok(page);
        }
        return Result.error("啥也查不到，啥也不是。。。");
    }

    @RequestMapping("insertItemParam")
    public Result insertItemParam(TbItemParam tbItemParam){
        Integer i = itemServiceFeign.insertItemParam(tbItemParam);
        if(i > 0){
            return Result.ok();
        }
        return Result.error("保存失败，你咋啥也不是呢。。。");
    }
}
