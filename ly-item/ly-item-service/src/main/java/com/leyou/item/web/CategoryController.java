package com.leyou.item.web;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    public ResponseEntity <List <Category>> queryCategoryListByPid(@RequestParam("pid") Long pid) {
        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }
    @GetMapping("names")
    public ResponseEntity<List<Category>> queryCnamesByCids(@RequestParam("ids")List<Long> cids){
        List <Category> categories = categoryService.queryCnameByCids(cids);
        return ResponseEntity.ok(categories);
    }
    @GetMapping("category")
    public ResponseEntity<List<Category>> queryCategoryByCid(@RequestParam("id")Long id){
        Category category = categoryService.queryCategoryById(id);
        Category category1 = categoryService.queryCategoryById(category.getParentId());
        Category category2 = categoryService.queryCategoryById(category1.getParentId());
        return ResponseEntity.ok(Arrays.asList(category2,category1,category));
    }


    //    - 分批查询spu的服务，已经写过。
//    - 第二：根据spuId查询sku的服务，已经写过
//- 第三：根据spuId查询SpuDetail的服务，已经写过
//- 第四：根据商品分类id，查询商品分类名称，没写过
//- 第五：根据商品品牌id，查询商品的品牌，没写过
}