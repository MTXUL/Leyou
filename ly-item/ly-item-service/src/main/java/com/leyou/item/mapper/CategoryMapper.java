package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;
//继承IdListMapper<Category,Long>可以通过批量id查询Category集合
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category,Long> {
}
