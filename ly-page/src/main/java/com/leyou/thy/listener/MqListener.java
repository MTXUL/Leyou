package com.leyou.thy.listener;

import com.leyou.thy.service.GoodsService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqListener {
    @Autowired
    private GoodsService goodsService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "spring.item.update.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.update","item.insert"}))
    public void updateOrInsertListener(Long spuId){
        goodsService.instancePage(spuId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "spring.item.delete.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.delete"}))
    public void deleteListener(Long spuId){
        goodsService.deletePage(spuId);
    }


}
