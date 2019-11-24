package com.leyou.es.listener;

import com.leyou.es.service.SearchService;
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
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "spring.item.update.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.update","item.insert"}))
    public void updateOrInsertListener(Long spuId){
        if(spuId!=null){
            searchService.createdIndex(spuId);
        }

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
        searchService.deleteIndex(spuId);
    }

}
