package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List <Category> queryCategoryListByPid(Long pid){
        Category category = new Category();
        category.setParentId(pid);
        List <Category> categoryList = categoryMapper.select(category);
        if(CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnum.PRICE_IS_NULL);
        }
        return categoryList;
    }

    public List<Category> queryCnameByCids(List<Long> cids) {
        List <Category> categories = categoryMapper.selectByIdList(cids);
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_BE_FOUND);
        }
        return categories;
    }
    public Category queryCategoryById(Long id){
        Category category = categoryMapper.selectByPrimaryKey(id);
        if (category==null){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_BE_FOUND);
        }
        return category;
    }

}
