package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     *
     * - page：当前页，int
     * - rows：每页大小，int
     * - sortBy：排序字段，String
     * - desc：是否为降序，boolean
     * - key：搜索关键词，String
     */
    @GetMapping("page")
    public ResponseEntity<PageResult <Brand>> queryCategoryListByPid(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",defaultValue = "false")Boolean desc,
            @RequestParam(value = "key",required = false)String key
    ){
        PageResult <Brand> brandPageResult = brandService.queryCategoryListByPid(page, rows, sortBy, desc, key);
//        System.out.println("11111111111111111111111111111111111111111");
//        System.out.println(brandPageResult.getItems().toString());
        return ResponseEntity.ok(brandPageResult);
    }
    @PostMapping("")
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam("cids") List<Long> cids){
//        201, "Created",此类型没有对象婴儿用build()
        brandService.saveBrand(brand,cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    //http://api.leyou.com/api/item/brand/cid/3
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsByCid(@PathVariable("cid")Long cid){
        return ResponseEntity.ok(brandService.queryBrandsByCid(cid));
    }
    //- 第五：根据商品品牌id，查询商品的品牌，没写过
    @GetMapping("bid")
    public ResponseEntity<Brand> queryBrandById(@RequestParam("id")Long id){
        Brand brand = brandService.queryBrandByBid(id);
        return ResponseEntity.ok(brand);
    }
    @GetMapping("list")
    public ResponseEntity<List <Brand>> queryBrandsByIds(@RequestParam("ids")List<Long> bids){
        return ResponseEntity.ok(brandService.queryBrandsByIds(bids));
    }

}


