package com.usian.controller;

import com.usian.mapper.TbItemMapper;
import com.usian.pojo.TbItem;
import com.usian.service.ItemService;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("service/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping("selectItemInfo")
    public TbItem selectItemInfo(Long itemId){
        return itemService.getById(itemId);
    }
}
