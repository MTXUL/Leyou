package com.leyou.es.clients;

import com.leyou.item.api.BrandApi;
import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface BrandClients extends BrandApi {
}
