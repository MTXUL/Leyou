package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "tb_spec_group")
@Data
public class SpecificationGroup {
    @Id
    private Long id;
    private Long cid;
    private String name;
    @Transient
    private List<SpecificationParam> params;
}