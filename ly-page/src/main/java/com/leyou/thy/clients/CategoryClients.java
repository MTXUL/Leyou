package com.leyou.thy.clients;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface CategoryClients extends CategoryApi {
}
