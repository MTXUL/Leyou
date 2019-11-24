package com.leyou.es.repository;

import com.leyou.es.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsRepository extends ElasticsearchRepository <Goods,Long>{
}
