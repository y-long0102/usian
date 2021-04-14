package com.usian.controller;

import com.usian.pojo.TbContentCategory;
import com.usian.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("service/contentCategory")
public class ContentCategoryController {

    @Autowired
    private ContentService contentService;

    @RequestMapping("selectContentCategoryByParentId")
    public List<TbContentCategory> selectContentCategoryByParentId(@RequestParam Long id){
        return contentService.selectContentCategoryByParentId(id);
    }

    @RequestMapping("insertContentCategory")
    public Integer insertContentCategory(@RequestBody TbContentCategory tbContentCategory){
        return contentService.insertContentCategory(tbContentCategory);
    }
}
