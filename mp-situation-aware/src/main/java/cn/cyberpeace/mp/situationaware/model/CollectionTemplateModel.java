package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;

import java.io.Serializable;

/**
 * mp_situation_collection_template
 * @author 
 */
public class CollectionTemplateModel extends BaseModel implements Serializable {

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板描述
     */
    private String description;

    /**
     * (单选) 操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other
     */
    private Integer osType;

    private static final long serialVersionUID = 1L;

}
