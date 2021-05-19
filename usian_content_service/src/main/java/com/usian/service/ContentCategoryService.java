package com.usian.service;

import com.usian.mapper.TbContentCategoryMapper;
import com.usian.pojo.TbContentCategory;
import com.usian.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentCategoryService {

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

    public Integer deleteContentCategoryById(Long categoryId) {
        // 根据ID查询出分类
        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(categoryId);
        // 判断当前分类是否是根，如果是根分类则拒绝删除，如果不是则走删除逻辑
        if(tbContentCategory.getParentId() == 0){
            // 因为当前分类为根分类，所以拒绝删除
            return 0;
        }
        // 删除当前分类的子分类
        this.deleteContentCategoryByParentId(categoryId);
        // 删除分类
        this.deleteById(categoryId);
        return 1;
    }

    /**
     * 根据传入的父级ID删除相应的分类
     * @param parentId 父级ID
     */
    private void deleteContentCategoryByParentId(Long parentId){
        // 根据父级ID查询所有子分类
        List<TbContentCategory> list = this.getContentCategoryByParentId(parentId);
        for (TbContentCategory tbContentCategory : list) {
            List<TbContentCategory> clist = this.getContentCategoryByParentId(tbContentCategory.getId());
            // 判断如果该分类有子分类
            if(clist != null && clist.size() > 0){
                this.deleteContentCategoryByParentId(tbContentCategory.getId());
            }
            // 删除当前分类
            this.deleteById(tbContentCategory.getId());
        }
    }

    /**
     * 根据父级ID查询子分类
     * @param parentId
     * @return
     */
    private List<TbContentCategory> getContentCategoryByParentId(Long parentId){
        TbContentCategoryExample example  = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId); // 最后生成为 where XXX and parentID = 传入值
        criteria.andStatusEqualTo(1);
        return tbContentCategoryMapper.selectByExample(example);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    private void deleteById(Long id){
        // 删除分类
        TbContentCategory t = new TbContentCategory();
        t.setId(id);
        t.setStatus(0);
        tbContentCategoryMapper.updateByPrimaryKeySelective(t);
    }
}
