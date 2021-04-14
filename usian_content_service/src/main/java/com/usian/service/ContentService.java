package com.usian.service;

import com.usian.mapper.TbContentCategoryMapper;
import com.usian.pojo.TbContentCategory;
import com.usian.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentService {

    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;

    public List<TbContentCategory> selectContentCategoryByParentId(Long id) {
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(id);
        criteria.andStatusEqualTo(1);
        return tbContentCategoryMapper.selectByExample(example);
    }

    /**
     * 添加内容分类
     * @param tbContentCategory
     * @return 添加成功记录数
     */
    public Integer insertContentCategory(TbContentCategory tbContentCategory) {
        Date date = new Date();
        tbContentCategory.setCreated(date);
        tbContentCategory.setUpdated(date);
        tbContentCategory.setStatus(1); // 设置删除状态
        return tbContentCategoryMapper.insert(tbContentCategory);
    }
}
