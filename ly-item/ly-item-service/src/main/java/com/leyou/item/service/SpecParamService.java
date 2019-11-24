package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecificationParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecParamService {

    @Autowired
    private SpecParamMapper specParamMapper;
    public List<SpecificationParam> findSpecificationParams(Long gid,Long cid,Boolean searching) {
        SpecificationParam specP = new SpecificationParam();
        specP.setGroupId(gid);
        specP.setCid(cid);
        specP.setSearching(searching);
        List <SpecificationParam> specificationParams = specParamMapper.select(specP);
        if(CollectionUtils.isEmpty(specificationParams)){
            throw new LyException(ExceptionEnum.SPECIFICATION_PARAMS_NOT_FOUND);
        }
        return specificationParams;

    }
}
