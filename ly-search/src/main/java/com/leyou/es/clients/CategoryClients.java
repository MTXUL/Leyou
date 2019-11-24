package com.leyou.es.clients;

import com.leyou.item.api.CategoryApi;
import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface CategoryClients extends CategoryApi {
}
