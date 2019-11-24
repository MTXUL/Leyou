package com.leyou.item.api;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    //    - 分批查询spu的服务，已经写过。
   @GetMapping("spu/page")
   PageResult <Spu> querySpuPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "key",required = false)String key
    );
    //    - 第二：根据spuId查询sku的服务，已经写过
    @GetMapping("/spu/detail/{sid}")
   SpuDetail querySpudetailBySid(@PathVariable("sid") Long sid);

    //- 第三：根据spuId查询SpuDetail的服务，已经写过
    @GetMapping("/sku/list")
   List <Sku> querySkusBySid(@RequestParam("id")Long sid);

    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id")Long sid);

    @GetMapping("sku/list/ids")
    List<Sku> querySkusByIds(@RequestParam("ids")List<Long> ids);
}


//- 第四：根据商品分类id，查询商品分类名称，没写过
//- 第五：根据商品品牌id，查询商品的品牌，没写过