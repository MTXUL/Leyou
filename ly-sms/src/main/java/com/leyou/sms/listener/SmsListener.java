package com.leyou.sms.listener;

import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.properties.SmsProperties;
import com.leyou.sms.utils.SmsUtilsHan;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsUtilsHan smsUtilsHan;
    @Autowired
    private SmsProperties prop;




    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "spring.sms.verify.code.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = "sms.verify.code"))
    public void updateOrInsertListener(Map<String,String> map) {
        if(CollectionUtils.isEmpty(map)){
            return;
        }
        String phone = map.remove("phone");
        if (StringUtils.isBlank(phone)){
            return;
        }
        smsUtilsHan.sendSms(phone,prop.getSignName(),prop.getTemplateCode(), JsonUtils.serialize(map));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "spring.user.sms.verify.code.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.user.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = "user.sms.verify"))
    public void smsListener(Map<String,String> map) {
        if(CollectionUtils.isEmpty(map)){
            return;
        }
        String phone = map.remove("phone");
        if (StringUtils.isBlank(phone)){
            return;
        }
        smsUtilsHan.sendSms(phone,prop.getSignName(),prop.getTemplateCode(), JsonUtils.serialize(map));
    }
}
