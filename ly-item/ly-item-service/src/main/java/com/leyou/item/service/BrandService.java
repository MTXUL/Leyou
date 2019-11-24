package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult <Brand> queryCategoryListByPid(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        PageHelper.startPage(page, rows);
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().orLike("name", "%" + key + "%").orEqualTo("letter", key.toUpperCase());
        }
        if (StringUtils.isNotBlank(sortBy)) {
            String clause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(clause);
        }
        List <Brand> brands = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        PageInfo <Brand> brandPageInfo = new PageInfo <>(brands);
//        System.out.println(brandPageInfo.toString());
        return new PageResult <Brand>(brandPageInfo.getTotal(), brands);
    }

    @Transactional
    public void saveBrand(Brand brand, List <Long> cids) {
//        首先将数据保存在tb_Bran表中brandMapper.insertSelective();此方法将属性为空的不会保存
        int insert = brandMapper.insert(brand);
        if(insert!=1){
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
//        再想中间表中保存数据
        for (Long cid : cids) {
            int i = brandMapper.saveCategoryBrand(cid, brand.getId());
            if(i!=1){
                throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
            }
        }
    }

    public Brand queryBrandByBid(Long brandId) {
        Brand brand = brandMapper.selectByPrimaryKey(brandId);
        if(brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    public List<Brand> queryBrandsByCid(Long cid) {
        List <Brand> brands = brandMapper.queryBrandsByCid(cid);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }


    public List<Brand> queryBrandsByIds(List<Long> bids) {
        List <Brand> brands = brandMapper.selectByIdList(bids);
        if (CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }
}
