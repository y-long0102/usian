package com.usian.service;

import com.usian.config.MQSender;
import com.usian.config.RedisClient;
import com.usian.mapper.*;
import com.usian.pojo.*;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class  OrderService {

    @Value("${ORDER_ID_KEY}")
    private String ORDER_ID_KEY;

    @Value("${ORDER_ID_BEGIN}")
    private Long ORDER_ID_BEGIN;

    @Value("${ORDER_ITEM_ID_KEY}")
    private String ORDER_ITEM_ID_KEY;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private LocalMessageMapper localMessageMapper;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public Long insertOrder(OrderInfo orderInfo) {
        Date date = new Date();
        // 1. 保存订单数据
        //     生成订单ID，  在哪生成？ 通过redis生成
        if(!redisClient.exists(ORDER_ID_KEY)){
            // 设置订单ID初始值
            redisClient.set(ORDER_ID_KEY, ORDER_ID_BEGIN);
        }
        Long orderId = redisClient.incr(ORDER_ID_KEY, 1);
        TbOrder tbOrder = orderInfo.getTbOrder();
        tbOrder.setOrderId(orderId.toString());
        // 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
        tbOrder.setStatus(1);
        tbOrder.setCreateTime(date);
        tbOrder.setUpdateTime(date);
        tbOrderMapper.insertSelective(tbOrder);
        // 2. 保存物流数据
        TbOrderShipping tbOrderShipping = orderInfo.getTbOrderShipping();
        tbOrderShipping.setOrderId(orderId.toString());
        tbOrderShipping.setCreated(date);
        tbOrderShipping.setUpdated(date);
        tbOrderShippingMapper.insertSelective(tbOrderShipping);
        // 3. 保存订单商品数据
        List<TbOrderItem> tbOrderItemList =
                JsonUtils.jsonToList(orderInfo.getOrderItem(), TbOrderItem.class);
        for (TbOrderItem tbOrderItem : tbOrderItemList) {
            Long oderItemId = redisClient.incr(ORDER_ITEM_ID_KEY, 1L);
            tbOrderItem.setId(oderItemId.toString());
            tbOrderItem.setOrderId(orderId.toString());
            tbOrderItemMapper.insertSelective(tbOrderItem);
        }

        // 将相应商品从购物车清除. 调用购物车服务清除相应购物车。

        // 将订单ID放入rabbitMQ的消息队列中
//        amqpTemplate.convertAndSend("order_exchage","order.add", orderId);
        //保存本地消息记录
        LocalMessage localMessage = new LocalMessage();
        localMessage.setTxNo(UUID.randomUUID().toString());
        localMessage.setOrderNo(orderId.toString());
        localMessage.setState(0);
        localMessageMapper.insertSelective(localMessage);

        //发布消息到mq，完成扣减库存
        mqSender.sendMsg(localMessage);

        return orderId;
    }

    public List<TbOrder> selectOvertimeOrder() {
        return tbOrderMapper.selectOvertimeOrder();
    }

    public void updateOrder(TbOrder tbOrder) {
        tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
    }

    public void updateTbItemByOrderId(String orderId) {
        TbOrderItemExample example = new TbOrderItemExample();
        TbOrderItemExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<TbOrderItem> tbOrderItems = tbOrderItemMapper.selectByExample(example);
        for (TbOrderItem tbOrderItem : tbOrderItems) {
            TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(tbOrderItem.getItemId()));
            System.out.println("修改的商品ID为：" + tbItem.getId());
            System.out.println("修改的商品title为：" + tbItem.getTitle());
            tbItem.setNum(tbItem.getNum() + tbOrderItem.getNum());
            tbItemMapper.updateByPrimaryKeySelective(tbItem);
        }
    }
}
