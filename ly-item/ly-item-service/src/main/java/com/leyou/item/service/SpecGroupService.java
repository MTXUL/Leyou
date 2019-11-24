package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.pojo.SpecificationGroup;
import com.leyou.item.pojo.SpecificationParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SpecGroupService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamService specParamService;
    public List<SpecificationGroup> findSpecsByCid(Long cid) {
        SpecificationGroup spec = new SpecificationGroup();
        spec.setCid(cid);
        List <SpecificationGroup> specs = specGroupMapper.select(spec);
        if(CollectionUtils.isEmpty(specs)){
            throw new LyException(ExceptionEnum.SPECIFICATION_GROUP_NOT_FOUND);
        }
        return specs;

    }
    public void insertSpecificationGroup(SpecificationGroup specificationGro) {
        int insert = specGroupMapper.insert(specificationGro);
        if(insert!=1){
            throw new LyException(ExceptionEnum.SPECIFICATION_GROUP_SAVE_FILE);
        }
    }


    public List<SpecificationGroup> querySpecGroupAndParamsByGid(Long id) {
        SpecificationGroup specificationGroup = new SpecificationGroup();
        specificationGroup.setCid(id);
        List <SpecificationGroup> groups = specGroupMapper.select(specificationGroup);
        List <SpecificationParam> Params = specParamService.findSpecificationParams(null, id, null);
        HashMap <Long, List <SpecificationParam>> map = new HashMap <>();
        for (SpecificationParam param : Params) {
//            判断map中是否存在当前gid
            if(!map.containsKey(param.getGroupId())){
//                不存在就新创建一个
                map.put(param.getGroupId(),new ArrayList <>());
            }
//            然后集合中添加元素
            map.get(param.getGroupId()).add(param);
        }
        for (SpecificationGroup group : groups) {
            group.setParams(map.get(group.getId()));
        }
        return groups;
    }
}
