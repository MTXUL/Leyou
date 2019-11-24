package com.leyou.thy.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@SpringBootTest
@RunWith(SpringRunner.class)
public class GoodsServiceTest {
    @Autowired
    private GoodsService goodsService;
    @Test
    public void test(){
        goodsService.instancePage(208L);
    }
}