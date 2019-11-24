package com.leyou.thy.service;

import com.leyou.item.pojo.*;
import com.leyou.thy.clients.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GoodsService {

    @Autowired
    private BrandClients brandClients;
    @Autowired
    private CategoryClients categoryClients;
    @Autowired
    private GoodsClients goodsClients;
    @Autowired
    private SpecGroupClients specGroupClients;
    @Autowired
    private SpecParamClients specParamClients;
    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> loadMode(Long id) {
            //spu skus detail brand category spec
        HashMap <String, Object> model = new HashMap <>();
        Spu spu = goodsClients.querySpuById(id);
        List <Sku> skus = spu.getSkus();
        SpuDetail detail = spu.getSpuDetail();
        List <Category> categories = categoryClients.queryCnamesByCids(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        Brand brand = brandClients.queryBrandById(spu.getBrandId());
        List <SpecificationGroup> specs = specGroupClients.querySpecGroupAndParamsByGid(spu.getCid3());
        model.put("spu",spu);
        model.put("skus",skus);
        model.put("detail",detail);
        model.put("brand",brand);
        model.put("specs",specs);
        model.put("categories",categories);
        return model;
    }
    public void instancePage(Long spuId){
        //上下文，携带数据
        Context context = new Context();
        context.setVariables(loadMode(spuId));
//        输出流
        File dest = new File("D:\\hm", spuId + ".html");
        if(dest.exists()){
            dest.delete();
        }
        log.info("【我只在执行】："+spuId+".html的生成"+dest.getAbsolutePath());
        try (PrintWriter writer = new PrintWriter(dest)){
            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            log.error("【页面静态化失败】:"+e);
        }

    }

    public void deletePage(Long spuId) {
        File dest = new File("D:\\hm", spuId + ".html");
        if(dest.exists()){
            dest.delete();
        }
    }
}
