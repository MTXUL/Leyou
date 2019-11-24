package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Transactional
    public PageResult <Spu> querySpuPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
         PageHelper.startPage(page, rows);
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //过滤
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if(saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //排序
        example.setOrderByClause("last_update_time DESC");

        //查询
        List <Spu> spus = spuMapper.selectByExample(example);

        //判断是否为空
        if(CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_BE_FOUND);
        }
        //解析id
        queryCidsAndBid(spus);
        //解析页面pageInfo
        PageInfo <Spu> info = new PageInfo <>(spus);
        return new PageResult <>(info.getTotal(),spus);
    }

    private void queryCidsAndBid(List<Spu> spus) {
        for (Spu spu : spus) {
            List <Category> categories = categoryService.queryCnameByCids(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            List <String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names,"/"));
            Brand brand = brandService.queryBrandByBid(spu.getBrandId());
            spu.setBname(brand.getName());
        }
    }

    /**此spu中包含了以下内容：
     *
     *      brandId	8557
     *      * cid1	74
     *      * cid2	75
     *      * cid3	76
     *      * skus	[…]
     *      * spuDetail	{…}
     *      * subTitle	大大大小小小
     *      * title	华为促销
     * @param spu
     */
    @Transactional
    public void saveGoods(Spu spu) {
        //spu
        spu.setCreateTime(new Date());
        spu.setValid(false);
        spu.setSaleable(true);
        spu.setLastUpdateTime(new Date());
        int count = spuMapper.insert(spu);
        if(count!=1){
            throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }

        //spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        int count1 = spuDetailMapper.insert(spuDetail);
        if(count1 !=1){
            throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }

       // spec_template
        List <Stock> stocks = new ArrayList <Stock>();
        //sku
        List <Sku> skus = spu.getSkus();
        for (Sku sku : skus) {

            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            sku.setSpuId(spu.getId());
            int count2 = skuMapper.insert(sku);
            if(count2 !=1){
                throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }

            //stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            System.out.println(stock.getSkuId());
            stock.setStock(sku.getStock());
            stocks.add(stock);
        }
        //大量stock保存注意此处有多少个stock对象就返回几，而不是一
        int count3 = stockMapper.insertList(stocks);

        if(count3 !=stocks.size()){
            throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        sendMessage(spu.getId(), "insert");

    }

    public SpuDetail querySpudetailBySid(Long sid) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(sid);
        if(spuDetail==null){
            throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        return spuDetail;
    }

    public List<Sku> querySkusBySid(Long sid) {
        //查询所有sku
        Sku sku = new Sku();
        sku.setSpuId(sid);
        List <Sku> skus = skuMapper.select(sku);
        if(CollectionUtils.isEmpty(skus)){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //遍历sku
        skus.forEach(s->{
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skus;
    }

    @Transactional
    public void updateGoods(Spu spu) {
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List <Sku> skus = skuMapper.select(sku);

        skus.forEach(s->{
            //删除stock
            int i = stockMapper.deleteByPrimaryKey(s.getId());
            if(i!=1){
                throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            //删除sku
            int i1 = skuMapper.deleteByPrimaryKey(s.getId());
            if(i1!=1){
                throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
        });
        //插入sku
        List <Sku> skus1 = spu.getSkus();
        if(CollectionUtils.isEmpty(skus1)){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }

        skus1.forEach(s->{
            s.setSpuId(spu.getId());
            s.setCreateTime(new Date());
            s.setLastUpdateTime(new Date());
            int insert = skuMapper.insert(s);
            if(insert!=1){
                throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            // 插入stock
            Stock stock = new Stock();
            stock.setStock(s.getStock());
            stock.setSkuId(s.getId());
            int insert1 = stockMapper.insert(stock);
            if(insert1!=1){
                throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
        });
//        修改spu
        spu.setValid(true);
        spu.setLastUpdateTime(new Date());
        int i1 = spuMapper.updateByPrimaryKey(spu);if(i1!=1){
            throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }

//        修改spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        int i = spuDetailMapper.updateByPrimaryKey(spuDetail);
        if(i!=1){
            throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        sendMessage(spu.getId(), "update");

//        //修改商品发送消息
//        try {
//            amqpTemplate.convertAndSend("item.update",spu.getId());
//        } catch (AmqpException e) {
//           log.error("【商品跟新消息发送失败】:"+e);
//        }
    }

    public Spu querySpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        Sku sku = new Sku();
        sku.setSpuId(id);
        List <Sku> skus = skuMapper.select(sku);
        spu.setSkus(skus);
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
        spu.setSpuDetail(spuDetail);
        if(spu==null){
            throw  new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        return spu;
    }

    public void deleteGoodsBySpuId(Long spuId) {
        int i = spuMapper.deleteByPrimaryKey(spuId);
        if(i!=1){
            throw new LyException(ExceptionEnum.GOODS_DETELE_ERROR);
        }
        sendMessage(spuId, "delete");
        //删除商品发送消息spuId

    }
    /**
     * 封装发送到消息队列的方法
     *
     * @param id
     * @param type
     */
    private void sendMessage(Long id, String type) {
        try {
            amqpTemplate.convertAndSend( "item." + type, id);
        } catch (Exception e) {
            log.error("{}商品消息发送异常，商品ID：{}", type, id, e);
        }
    }

    public List<Sku> querySkusByIds(List<Long> ids) {
        List <Sku> skus = skuMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(skus)){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //遍历sku
        skus.forEach(s->{
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skus;

    }
}
