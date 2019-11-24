package com.leyou.thy.clients;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface BrandClients extends BrandApi {
}
