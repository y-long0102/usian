package com.usian.controller;

import com.usian.feign.ContentFeign;
import com.usian.pojo.TbContent;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("content")
public class ContentController {

    @Autowired
    private ContentFeign contentFeign;

    @RequestMapping("selectTbContentAllByCategoryId")
    public Result selectTbContentAllByCategoryId(Long categoryId){
        PageResult pageResult = contentFeign.selectTbContentAllByCategoryId(categoryId);
        if(pageResult != null){
            return Result.ok(pageResult);
        }
        return Result.error("查询失败，请联系岳龙。。。");
    }
}
