package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int saveCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);
    @Select("select b.* from tb_category_brand cb inner join tb_brand b on b.id=cb.brand_id where cb.category_id=#{cid}")
    List<Brand> queryBrandsByCid(@Param("cid")Long cid);

}
