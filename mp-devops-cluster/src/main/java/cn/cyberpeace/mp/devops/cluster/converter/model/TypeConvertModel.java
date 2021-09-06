package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.Data;

@Data
public class TypeConvertModel {

    /**
     * 类型ID
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 2级类型ID
     */
    private Integer l2ParentId;

    /**
     * 1级类型ID
     */
    private Integer l1ParentId;

}
