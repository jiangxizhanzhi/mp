package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;

/**
 * 载荷同步表(MpDevopsLoad)实体类
 */
@Data
@Table("mp_devops_load")
public class DevopsLoadModel extends BaseModel {

    /**
     * 主键
     */
    private Integer pk;
    /**
     * ID
     */
    private String id;
    /**
     * 漏洞编号
     */
    private String vulneCode;
    /**
     * 脚本文件地址
     */
    private String scriptFilePath;
    /**
     * 脚本文件名称
     */
    private String scriptFile;
    /**
     * 脚本类型
     */
    private Integer scriptType;
    /**
     * 脚本代码
     */
    private String scriptCode;
    /**
     * 技战法
     */
    private Integer technology;
}