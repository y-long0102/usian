package com.usian.listener;

import com.usian.service.SearchItemService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SearchMQListener {

    @Autowired
    private SearchItemService searchItemService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search_queue", durable = "true"),
            exchange = @Exchange(value = "item_exchage", type = ExchangeTypes.TOPIC),
            key = "item.*"
    ))
    public void listen(String msg) throws IOException {
        System.out.println("接收到消息：" + msg);
        int result = searchItemService.insertDocument(msg);
        if(result>0){
            throw new RuntimeException("同步失败");
        }
    }
}
