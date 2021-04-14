package com.usian.controller;

import com.usian.feign.ContentFeign;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("content")
public class ContentCategoryController {

    @Autowired
    private ContentFeign contentFeign;

    @RequestMapping("selectContentCategoryByParentId")
    public Result selectContentCategoryByParentId(@RequestParam(defaultValue = "0") Long id){
        List<TbContentCategory> list =  contentFeign.selectContentCategoryByParentId(id);
        if(list != null && list.size() > 0){
            return Result.ok(list);
        }
        return Result.error("咋又不好使了，。。完犊子，，，");
    }

    @RequestMapping("insertContentCategory")
    public Result insertContentCategory(TbContentCategory tbContentCategory){
        Integer i = contentFeign.insertContentCategory(tbContentCategory);
        if(i == 1){
            return Result.ok();
        }
        return Result.error("添加分类失败，，，完犊子了。。。");
    }
}
