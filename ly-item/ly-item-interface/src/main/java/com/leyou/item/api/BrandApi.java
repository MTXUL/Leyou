package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("brand")
public interface BrandApi {
    //- 第五：根据商品品牌id，查询商品的品牌，没写过
    @GetMapping("bid")
   Brand queryBrandById(@RequestParam("id")Long id);
    @GetMapping("list")
    List <Brand> queryBrandsByIds(@RequestParam("ids")List<Long> bids);
}
