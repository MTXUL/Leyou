package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestParam()
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     *分页查询spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "key",required = false)String key
    ) {
        PageResult <Spu> spuPageResult = goodsService.querySpuPage(page, rows, saleable, key);
        return ResponseEntity.ok(spuPageResult);
    }

    /**
     * 保存商品
     * @param spu
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据Spu的id查询spuDetail
     * @param sid
     * @return
     */
//    http://api.leyou.com/api/item/spu/detail/208
    @GetMapping("/spu/detail/{sid}")
    public ResponseEntity<SpuDetail> querySpudetailBySid(@PathVariable("sid") Long sid){
        return ResponseEntity.ok(goodsService.querySpudetailBySid(sid));
    }

    /**
     *根据spuId查询skuList
     * @param sid
     * @return
     */
    //http://api.leyou.com/api/item/sku/list?id=208
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkusBySid(@RequestParam("id")Long sid){
        return ResponseEntity.ok(goodsService.querySkusBySid(sid));
    }

    /**
     * 跟新商品
     *
     * @param spu
     * @return
     */
    //http://api.leyou.com/api/item/goods
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
        goodsService.updateGoods(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
//    - 分批查询spu的服务，已经写过。
//    - 第二：根据spuId查询sku的服务，已经写过
//- 第三：根据spuId查询SpuDetail的服务，已经写过
//- 第四：根据商品分类id，查询商品分类名称，没写过
//- 第五：根据商品品牌id，查询商品的品牌，没写过

    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id")Long sid){
        return ResponseEntity.ok(goodsService.querySpuById(sid));
    }
    @DeleteMapping("spu/{id}")
    public ResponseEntity<Void> deleteGoodsBySpuId(@PathVariable Long spuId){
        goodsService.deleteGoodsBySpuId(spuId);
        return new ResponseEntity <>(HttpStatus.MOVED_PERMANENTLY);

    }

    @GetMapping("sku/list/ids")
    public ResponseEntity<List<Sku>> querySkusByIds(@RequestParam("ids")List<Long> ids){
        return ResponseEntity.ok(goodsService.querySkusByIds(ids));
    }


}
