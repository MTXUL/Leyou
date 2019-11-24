package com.leyou.item.api;

import com.leyou.item.pojo.SpecificationParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("spec")
public interface SpecParamApi {
    @GetMapping("params")
    List <SpecificationParam> findSpecificationParamsByGid(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching
    );
}
