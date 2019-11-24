package com.leyou.item.web;

import com.leyou.item.pojo.SpecificationGroup;
import com.leyou.item.service.SpecGroupService;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecGroupController {
    @Autowired
    private SpecGroupService specGroupService;
    @Autowired
    private SpecParamService specParamService;
    @GetMapping("groups/{cid}")
    public ResponseEntity<List <SpecificationGroup>> findSpecsByCid(@PathVariable("cid")Long cid){
        return ResponseEntity.ok(specGroupService.findSpecsByCid(cid));
    }
    @PostMapping("group")
    public ResponseEntity<Void> insertSpecificationGroup(@RequestBody SpecificationGroup specificationGroup){
        System.out.println(specificationGroup);
        specGroupService.insertSpecificationGroup(specificationGroup);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/group_params/{id}")
    public ResponseEntity<List<SpecificationGroup>> querySpecGroupAndParamsByGid(@PathVariable("id")Long id){
        return ResponseEntity.ok(specGroupService.querySpecGroupAndParamsByGid(id));
    }

}
