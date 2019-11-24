package com.leyou.es.repository;

import com.leyou.common.vo.PageResult;
import com.leyou.es.clients.GoodsClients;
import com.leyou.es.pojo.Goods;
import com.leyou.es.service.SearchService;
import com.leyou.item.pojo.Spu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {
    @Autowired
    private GoodsClients goodsClients;
    @Autowired
    private SearchService searchService;
    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private GoodsRepository goodsRepository;


    @Test
    public void creatIndex(){
        //创建索引库
        template.createIndex(Goods.class);
        //创建映射
        template.putMapping(Goods.class);
    }
    @Test
    public void saveData(){
        int page=1;
        int rows=100;
        int size=0;
        //查询所有spu
        do{
            PageResult <Spu> spus = goodsClients.querySpuPage(page, rows, true, null);
            List <Spu> items = spus.getItems();
            if(CollectionUtils.isEmpty(items)){
                break;
            }
            //        形成goods对象
            List <Goods> goodsList = items.stream().map(searchService::saveGoods).collect(Collectors.toList());
            //        保存goods
            goodsRepository.saveAll(goodsList);
            size = items.size();
            page++;
        }while (size==100);











    }

}