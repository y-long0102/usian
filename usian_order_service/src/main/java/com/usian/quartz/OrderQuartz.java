package com.usian.quartz;

import com.usian.config.MQSender;
import com.usian.pojo.LocalMessage;
import com.usian.pojo.TbOrder;
import com.usian.service.LocalMessageService;
import com.usian.service.OrderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class OrderQuartz implements Job {

    @Autowired
    private OrderService orderService;

    @Autowired
    private LocalMessageService localMessageService;

    @Autowired
    private MQSender mqSender;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Date date = new Date();
        System.out.println("删除无效订单任务执行开始。。。" + date);
        // 查询超时订单
        List<TbOrder> tbOrders = orderService.selectOvertimeOrder();
        for (TbOrder tbOrder : tbOrders) {
            System.out.println("处理超时订单的订单号为：" + tbOrder.getOrderId());
            tbOrder.setUpdateTime(date);
            tbOrder.setCloseTime(date);
            tbOrder.setEndTime(date);
            // 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
            tbOrder.setStatus(6);
            orderService.updateOrder(tbOrder);
            // 处理库存， 将商品库存数增加回来。
            orderService.updateTbItemByOrderId(tbOrder.getOrderId());
        }
        /*----------------------------检查本地消息表----------------------------------------*/
        List<LocalMessage> localMessages = localMessageService.selectlocalMessageByStatus(0);
        for (LocalMessage localMessage : localMessages) {
            mqSender.sendMsg(localMessage);
        }

        System.out.println("删除无效订单任务执行结束。。。" + date);
    }
}
