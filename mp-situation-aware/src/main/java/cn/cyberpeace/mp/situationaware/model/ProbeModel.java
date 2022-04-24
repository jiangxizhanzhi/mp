package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

/**
 * mp_situation_probe
 * @author 
 */
@Data
public class ProbeModel extends BaseModel {

    public static final int TYPE_COMMON = 0;

    public static final int TYPE_CUSTOM = 1;

    public static final int CATATORY_BASIC_INFO = 0;

    public static final int CATATORY_EVENT = 1;

    public static final int CATATORY_LOG = 2;


    /**
     * 探针名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 0:信息类 1:事件类 2:日志类 
     */
    private Integer probeCategory;

    /**
     * 0:通用探针 1:自定义探针
     */
    private Integer type;

    /**
     * 自定义探针输出格式: none, json, plain
     */
    private String outputFormat;

    /**
     * 1:删除 0:未删除
     */
    private Integer isDeleted;

    /**
     * (可多选)操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other
     */
    private String osType;


    private static final long serialVersionUID = 1L;

}
