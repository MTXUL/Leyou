package com.leyou.thy.clients;

import com.leyou.item.api.SpecParamApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface SpecParamClients extends SpecParamApi {
}
