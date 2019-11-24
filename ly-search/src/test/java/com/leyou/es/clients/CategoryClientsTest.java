package com.leyou.es.clients;


import com.leyou.item.pojo.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientsTest {

    @Autowired
    private CategoryClients categoryClients;
    @Test
    public void  createDeployment() {

        List <Long> strings = Arrays.asList(1L,2L,3L);
        List <Category> categories = categoryClients.queryCnamesByCids(strings);
        for (Category category : categories) {
            System.out.println(categories.toString());
        }

    }

}
