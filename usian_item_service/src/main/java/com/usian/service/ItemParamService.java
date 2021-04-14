package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ItemParamService {

    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    public TbItemParam selectItemParamByItemCatId(Long itemCatId) {
        TbItemParamExample example = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = example.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> tbItemParams = tbItemParamMapper.selectByExampleWithBLOBs(example);
        if(tbItemParams != null && tbItemParams.size() > 0){
            return tbItemParams.get(0);
        }
        return null;
    }

    public PageResult selectItemParamAll() {
        PageHelper.startPage(1, 9999999);
        List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(null);
        PageInfo<TbItemParam> pageInfo = new PageInfo<>(list);
        return new PageResult(1, pageInfo.getTotal(), pageInfo.getList());
    }

    public Integer insertItemParam(TbItemParam tbItemParam) {
        Date date = new Date();
        tbItemParam.setCreated(date);
        tbItemParam.setUpdated(date);
        return tbItemParamMapper.insert(tbItemParam);
    }
}
