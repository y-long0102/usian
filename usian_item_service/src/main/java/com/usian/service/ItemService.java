package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.config.RedisClient;
import com.usian.mapper.*;
import com.usian.pojo.*;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisClient redisClient;

    @Value("{ITEMO_IFNO}")
    private String ITEMO_IFNO;

    @Value("{BASE}")
    private String BASE;

    public TbItem getById(Long itemId) {
        String key = ITEMO_IFNO + ":" + itemId + ":" + BASE;
        // 判断当前itemId是否在缓存中存在
        TbItem tbItem = (TbItem)redisClient.get(key);
        if(tbItem != null){
            // 如果存在则直接从缓存中获取并返回
            return tbItem;
        }
        // 如果不存在则从数据库中查询
        tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        // 如果查询结果不为空则把数据放入缓存中并返回、
        if(tbItem != null){
            redisClient.set(key, tbItem);
            return tbItem;
        }
        // 如果查询结果为空，为了避免缓存穿透问题， 将查询为空的itemId，key为正常存储itemId的key，值为null，并设置缓存失效时间
        redisClient.set(key, tbItem);
        redisClient.expire(key, 60);
        // 如果查询结果为空则直接返回空数据
        return tbItem;
    }

    public PageResult selectTbItemAllByPage(Integer page, Integer rows) {
        // 创建分页插件 pageHelper
        PageHelper.startPage(page, rows);
        TbItemExample example = new TbItemExample();
        // 根据更新时间倒叙查询
        example.setOrderByClause("updated desc");
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo((byte)1);
        // 查询
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
        PageInfo<TbItem> tbItemPageInfo = new PageInfo<>(tbItems);
        return new PageResult(page, tbItemPageInfo.getTotal(), tbItemPageInfo.getList());
    }

    public Integer insertTbItem(TbItem tbItem, String desc, String itemParams) {
        Date date = new Date();
        Long itemId = IDUtils.genItemId();
        tbItem.setId(itemId);// 设置ID
        tbItem.setCreated(date);// 设置创建时间
        tbItem.setUpdated(date);// 设置修改时间
        tbItem.setStatus((byte)1);// 设置删除状态为未删除
        // 保存商品
        int i1 = tbItemMapper.insert(tbItem);
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        int i2 = tbItemDescMapper.insert(tbItemDesc);// 保存商品描述
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setItemId(itemId);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setUpdated(date);
        int i3 = tbItemParamItemMapper.insert(tbItemParamItem);// 保存商品参数

        amqpTemplate.convertAndSend("item_exchage","item.add",itemId);
        return i1 + i2 + i3;
    }

    public Map<String, Object> preUpdateItem(Long itemId) {
        Map<String, Object> map = new HashMap<>();
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        map.put("item", tbItem);
        TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
        map.put("itemDesc", tbItemDesc.getItemDesc());
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(example);
        if(tbItemParamItems != null && tbItemParamItems.size() > 0){
            map.put("itemParamItem", tbItemParamItems.get(0).getParamData());
        }
        TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbItem.getCid());
        map.put("itemCat", tbItemCat.getName());
        return map;
    }

    public TbItemDesc selectItemDescByItemId(Long itemId) {
        return tbItemDescMapper.selectByPrimaryKey(itemId);
    }

    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId) {
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(example);
        if(tbItemParamItems != null && tbItemParamItems.size() > 0){
            return tbItemParamItems.get(0);
        }
        return null;
    }

    public Integer updateTbItemByOrderId(String msg) {
        // 根据订单ID查询出所有订单商品
        TbOrderItemExample example = new TbOrderItemExample();
        TbOrderItemExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(msg);
        List<TbOrderItem> tbOrderItems = tbOrderItemMapper.selectByExample(example);
        // 定义返回值i
        Integer i = 0;
        for (TbOrderItem tbOrderItem : tbOrderItems) {
            // 根据商品ID获取商品
            TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(tbOrderItem.getItemId()));
            tbItem.setNum(tbItem.getNum() - tbOrderItem.getNum());
            i += tbItemMapper.updateByPrimaryKeySelective(tbItem);
        }
        return i;
    }
}
