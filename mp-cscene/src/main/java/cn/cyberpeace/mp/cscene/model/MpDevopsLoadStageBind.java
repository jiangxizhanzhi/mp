package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.io.Serializable;

/**
 * 载荷阶段绑定表(MpDevopsLoadStageBind)实体类
 */
@Data
@Table("mp_devops_load_stage_bind")
public class MpDevopsLoadStageBind extends BaseModel {
    /**
     * 主键
     */
    private Integer pk;
    /**
     * 绑定类型
     */
    private String addType;
    /**
     * 场景id
     */
    private String sceneId;
    /**
     * 阶段id
     */
    private String stageId;
    /**
     * 载荷id
     */
    private String loadId;
    /**
     * 界面类型
     */
    private String interfaceType;
    /**
     * 部署类型
     */
    private String deployment;
    /**
     * 参数列表
     */
    private String paramList;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}

