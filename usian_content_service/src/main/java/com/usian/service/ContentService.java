package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.config.RedisClient;
import com.usian.mapper.TbContentMapper;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentExample;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContentService {

    @Value("${AD_CATEGORY_ID}")
    private Long AD_CATEGORY_ID;

    @Value("${AD_HEIGHT}")
    private Integer AD_HEIGHT;

    @Value("${AD_WIDTH}")
    private Integer AD_WIDTH;

    @Value("${AD_HEIGHTB}")
    private Integer AD_HEIGHTB;

    @Value("${AD_WIDTHB}")
    private Integer AD_WIDTHB;

    @Autowired
    private TbContentMapper tbContentMapper;

    @Autowired
    private RedisClient redisClient;

    @Value("${PORTAL_AD_KEY}")
    private String portal_ad_redis_key;


    public PageResult selectTbContentAllByCategoryId(Long categoryId) {
        PageHelper.startPage(1, 999999);
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);
        return new PageResult(1, pageInfo.getTotal(), pageInfo.getList());
    }

    public List<AdNode> selectFrontendContentByAD() {
        // 先从缓存redis中获取广告
        List<AdNode> redisAdNodeList = (List<AdNode>)redisClient.hget(portal_ad_redis_key, AD_CATEGORY_ID.toString());
        if(redisAdNodeList != null){
            System.out.println("我是从redis中取到的广告数据。。。");
            return redisAdNodeList;
        }
        System.out.println("我要从mysql中取到的广告数据。。。");
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(AD_CATEGORY_ID);
        List<TbContent> list = tbContentMapper.selectByExample(example);

        List<AdNode> adNodeList = new ArrayList<>();
        for (TbContent tbContent : list) {
            AdNode adNode = new AdNode();
            adNode.setSrc(tbContent.getPic());
            adNode.setSrcB(tbContent.getPic2());
            adNode.setHref(tbContent.getUrl());
            adNode.setHeight(AD_HEIGHT);
            adNode.setWidth(AD_WIDTH);
            adNode.setHeightB(AD_HEIGHTB);
            adNode.setWidthB(AD_WIDTHB);
            adNodeList.add(adNode);
        }

        // 将从数据库中查询出的内容放入redis中
        redisClient.hset(portal_ad_redis_key, AD_CATEGORY_ID.toString(), adNodeList);
        return adNodeList;
    }
}
