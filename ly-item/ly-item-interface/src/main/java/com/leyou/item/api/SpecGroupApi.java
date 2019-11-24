package com.leyou.item.api;

import com.leyou.item.pojo.SpecificationGroup;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("spec")
public interface SpecGroupApi {
    @GetMapping("group_params/{id}")
    List<SpecificationGroup> querySpecGroupAndParamsByGid(@PathVariable("id")Long id);
}
