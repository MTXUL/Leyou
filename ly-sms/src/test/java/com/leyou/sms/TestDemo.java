package com.leyou.sms;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDemo {

    @Test
    public void fun(){
        int sum = 0 ;
        for (int x = 0 ; x < 10 ; x ++) {
            sum += x ;
            if (x % 3 == 0) {
                break ;
            }
        }
        System.out.println(sum) ;
    }
    @Test
    public void fun2() throws InterruptedException {
        final Long TIME_BLANK= 6200L;
        String time1 = String.valueOf(System.currentTimeMillis());
        System.out.println(time1);
        Thread.sleep(6000L);
        String time2 = String.valueOf(System.currentTimeMillis());
        System.out.println(time2);
        if (Long.valueOf(time2)-Long.valueOf(time1)<=TIME_BLANK){
            System.out.println("【真的小于6秒啊！】");
        }else {
            System.out.println("【哈哈，你猜错了！！】");
        }

    }
}
