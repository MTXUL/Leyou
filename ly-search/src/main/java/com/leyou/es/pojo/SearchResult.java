package com.leyou.es.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;

import java.util.List;
import java.util.Map;

public class SearchResult extends PageResult<Goods> {
    private List <Brand> brands;
    private List<Category> categories;
    private List<Map <String,Object>> specs;//商品分类规格

    public List <Map <String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List <Map <String, Object>> specs) {
        this.specs = specs;
    }

    public SearchResult() {
    }

    public List <Brand> getBrands() {
        return brands;
    }

    public void setBrands(List <Brand> brands) {
        this.brands = brands;
    }

    public List <Category> getCategories() {
        return categories;
    }

    public void setCategories(List <Category> categories) {
        this.categories = categories;
    }

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List <Brand> brands, List <Category> categories,List<Map <String,Object>> specs) {
        super(total, totalPage, items);
        this.brands = brands;
        this.categories = categories;
        this.specs=specs;
    }
}
