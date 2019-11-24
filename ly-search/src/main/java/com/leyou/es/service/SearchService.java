package com.leyou.es.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.es.clients.BrandClients;
import com.leyou.es.clients.CategoryClients;
import com.leyou.es.clients.GoodsClients;
import com.leyou.es.clients.SpecParamClients;
import com.leyou.es.pojo.Goods;
import com.leyou.es.pojo.SearchRequest;
import com.leyou.es.pojo.SearchResult;
import com.leyou.es.repository.GoodsRepository;
import com.leyou.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchService {
    @Autowired
    private BrandClients brandClients;
    @Autowired
    private CategoryClients categoryClients;
    @Autowired
    private GoodsClients goodsClients;
    @Autowired
    private SpecParamClients specParamClients;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate template;



    public Goods saveGoods(Spu spu){
//        抽取
        Long spuId=spu.getId();
        //处理分类名称
        List <Category> categories = categoryClients.queryCnamesByCids(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        List <String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        String join = StringUtils.join(names, " ");

//        处理setAll
        String all=spu.getTitle()+join+brandClients.queryBrandById(spu.getBrandId()).getName();

//        sku信息的json结构
        List <Map<String,Object>> objects = new ArrayList <>();
        List <Long> priceList = new ArrayList <>();
        List <Sku> skus = goodsClients.querySkusBySid(spuId);
        for (Sku sku : skus) {
            HashMap <String, Object> skuMap = new HashMap <>();
            skuMap.put("id",sku.getId());
            skuMap.put("title",sku.getTitle());
            skuMap.put("price",sku.getPrice());
            skuMap.put("image",StringUtils.substringBefore(sku.getImages(),","));
            objects.add(skuMap);
            priceList.add(sku.getPrice());
        }
        String skuJson = JsonUtils.serialize(objects);



//        处理setSpecs Map<String, Object> specs 可搜索的规格参数，key是参数名，值是参数值
        HashMap <String, Object> spec = new HashMap <>();
//        key是参数名
        List <SpecificationParam> specificationParams = specParamClients.findSpecificationParamsByGid(null, spu.getCid3(), true);
        if(CollectionUtils.isEmpty(specificationParams)){
            throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
//        value分为通用值，特殊值
        SpuDetail spuDetail = goodsClients.querySpudetailBySid(spuId);
        if(spuDetail==null){
            throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
//        通用属性
        String genericSpec = spuDetail.getGenericSpec();
        Map <String, Object> genericMap = JsonUtils.parseMap(genericSpec, String.class, Object.class);
        if(CollectionUtils.isEmpty(genericMap)){
            throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
//        特殊属性
        String specialSpec = spuDetail.getSpecialSpec();
        Map <String, List<String>> specialMap = JsonUtils.nativeRead(specialSpec, new TypeReference <Map <String, List<String>>>(){});
        Map <String, Object> specs = new HashMap <>();
        for (SpecificationParam specificationParam : specificationParams) {
            String key = specificationParam.getName();
            Object value="";
            //判断参数是通用的还是特殊的
            if(specificationParam.getGeneric()){
                value=genericMap.get(specificationParam.getId().toString());
                if(specificationParam.getNumeric()){
                    value= chooseSegment(value.toString(), specificationParam);
                }
            }else {
                value=specialMap.get(specificationParam.getId().toString());
            }
            specs.put(key,value);

        }
        Goods goods = new Goods();
        goods.setId(spuId);
        goods.setAll(all);//包含标题，分类，甚至品牌
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setPrice(priceList);// List <Long>
        goods.setSkus(skuJson); // sku信息的json结构
        goods.setSpecs(specs);//  Map <String, Object> specs 可搜索的规格参数，key是参数名，值是参数值
        goods.setSubTitle(spu.getSubTitle());//// 卖点
        return goods;
    }
    private String chooseSegment(String value, SpecificationParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

//    private String key;// 搜索条件
//    private Integer page;// 当前页
//    private static final Integer DEFAULT_SIZE = 20;// 每页大小，不从页面接收，而是固定大小
//    private static final Integer DEFAULT_PAGE = 1;// 默认页
    public SearchResult search(SearchRequest searchRequest) {


//        由于elatic的page从0开始
        int page = searchRequest.getPage()-1;
        int size = searchRequest.getSize();

        //创建构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //        查询字段过滤
        queryBuilder.withSourceFilter( new FetchSourceFilter(new String[] {"id","skus","subTitle"},null));
        //基础查询
//        QueryBuilder basicQuery = QueryBuilders.matchQuery("all", searchRequest.getKey());
        QueryBuilder basicQuery=buildBasicQuery(searchRequest);
        queryBuilder.withQuery(basicQuery);

        //        分页
        queryBuilder.withPageable(PageRequest.of(page,size));
        //        排序
        String sortBy = searchRequest.getSortBy();
        Boolean descending = searchRequest.getDescending();
        if(StringUtils.isNotBlank(sortBy)){
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending ? SortOrder.DESC:SortOrder.ASC));

        }
        //聚和分类和品牌
        String aggCategory="agg_category";
        String aggBrand="agg_brand";
        queryBuilder.addAggregation(AggregationBuilders.terms(aggCategory).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(aggBrand).field("brandId"));


//

//        聚合查询只能使用elasticsearchTemplate
        AggregatedPage <Goods> search = template.queryForPage(queryBuilder.build(), Goods.class);
//        解析结果,Aggregation是一个接口，无法获取到桶，要用其实现类LongTerms
        LongTerms aggregation = (LongTerms)search.getAggregation(aggCategory);
        LongTerms aggregation1 = (LongTerms)search.getAggregation(aggBrand);

        //获取每个聚合的桶，然后从桶中获取相关的id值
        List <Long> cids = aggregation.getBuckets().stream().map(s ->s.getKeyAsNumber().longValue()).collect(Collectors.toList());
        List <Category> categories = categoryClients.queryCnamesByCids(cids);
        List <Long> bids = aggregation1.getBuckets().stream().map(s -> s.getKeyAsNumber().longValue()).collect(Collectors.toList());
        List <Brand> brands=brandClients.queryBrandsByIds(bids);
//        Page <Goods> search = goodsRepository.search(queryBuilder.build());
//        List <Goods> collect = search.stream().collect(Collectors.toList());
//        if(CollectionUtils.isEmpty(collect)){
//            throw
//        }
        List<Map <String,Object>> specs=null;
        if(categories!=null && categories.size()==1){
            //聚合参数规格,需要确定分类才能聚合参数规格
            Category category=categories.get(0);

//            注意要在原有的搜索字段上进行聚合
            specs=aggSpecs(category,basicQuery);
        }


        long total = search.getTotalElements();
        int totalPage=page(total);
        List <Goods> items = search.getContent();

        return new SearchResult(total,totalPage,items,brands,categories,specs);// Long total, Long totalPage, List<T> items)
    }

    private QueryBuilder buildBasicQuery(SearchRequest searchRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", searchRequest.getKey()));
        //规格参数的过滤
        Map <String, String> filter = searchRequest.getGuoLv();
        if(!CollectionUtils.isEmpty(filter)){
            for (Map.Entry <String,String> f:filter.entrySet()){
                //CPU品牌:"海思（Hisilicon）"
                //brandId:"2032"
                //cid3:"76"
                //1.判断key是否为brandId
                String key=f.getKey();
                if (!StringUtils.equals(f.getKey(),"cid3") && !StringUtils.equals(f.getKey(),"brandId")){
                    key="specs." + f.getKey() + ".keyword";
                }
                boolQueryBuilder.filter(QueryBuilders.termQuery(key,f.getValue()));
            }
        }
        return boolQueryBuilder;
//        过滤
//        NativeSearchQueryBuilder all = queryBuilder.withQuery(QueryBuilders.matchQuery("all", searchRequest.getKey()));
        //        参数规格过滤

    }

    private List<Map<String, Object>> aggSpecs(Category category,QueryBuilder basicQuery) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);

        List<Map <String,Object>> specs=new ArrayList<>();
        //根据商品分类cid3查找对应的规格参数
        List <SpecificationParam> specificationParams = specParamClients.findSpecificationParamsByGid(null, category.getId(), true);
        //遍历规格参数，为每个规格参数进行聚合
        for (SpecificationParam specificationParam : specificationParams) {
//            、、每个规格参数进行聚合
            String aggName="agg_"+specificationParam.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(aggName).field("specs." + specificationParam.getName() + ".keyword"));
            AggregatedPage <Goods> aggs = template.queryForPage(queryBuilder.build(), Goods.class);
            //解析
            StringTerms agg = (StringTerms)aggs.getAggregation(aggName);

            List <String> options = agg.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
            Map<String, Object> map = new HashMap <>();
            map.put("k",specificationParam.getName());
            map.put("options",options);
            specs.add(map);
        }
        return specs;
    }

    private int page(long total){
        boolean a= total%20==0;
        return  a ? (int)total/20 : (int)Math.ceil(total/20)+1;
    }

    public void createdIndex(Long spuId) {
        //构建商品
        Spu spu = goodsClients.querySpuById(spuId);
        if(spu==null){
            log.error("【索引库中不包含此商品】");
            throw new RuntimeException();
        }
        try {
            Goods goods = saveGoods(spu);
            //保存商品
            goodsRepository.save(goods);
        } catch (Exception e) {
            log.error("【商品构建失败】");
        }
    }

    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
