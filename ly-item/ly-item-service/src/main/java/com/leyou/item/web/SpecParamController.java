package com.leyou.item.web;

import com.leyou.item.pojo.SpecificationParam;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecParamController {
    @Autowired
    private SpecParamService specParamService;
    //http://api.leyou.com/api/item/spec/params?cid=76
    @GetMapping("params")
    public ResponseEntity<List<SpecificationParam>> findSpecificationParamsByGid(
            @RequestParam(value = "gid",required = false)Long gid,
            @RequestParam(value = "cid",required = false)Long cid,
            @RequestParam(value = "searching",required = false)Boolean searching
            ){
            return ResponseEntity.ok(specParamService.findSpecificationParams(gid,cid,searching));
    }
}
