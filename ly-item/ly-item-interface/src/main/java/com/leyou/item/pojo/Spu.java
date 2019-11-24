package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "tb_spu")
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String title;// 标题

    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    @JsonIgnore
    private Boolean valid;// 是否有效，逻辑删除用
    private Date createTime;// 创建时间
    @JsonIgnore //加上次注解不会向页面上传递次数据
    private Date lastUpdateTime;// 最后修改时间


//    应为页面要返回目录名称
    @Transient//因为数据库中没有此字段，故要加上此注解
    private  String cname;
    @Transient
    private  String bname;

    @Transient
    private List <Sku> skus;
    @Transient
    private SpuDetail spuDetail;

    /**
     * http://api.leyou.com/api/item/goods
     * POST
     *
     * brandId	8557
     * cid1	74
     * cid2	75
     * cid3	76
     * skus	[…]
     * spuDetail	{…}
     * subTitle	大大大小小小
     * title	华为促销
     */
}