package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 载荷同步表(MpDevopsLoad)实体类
 * @since 2024-03-28 16:41:10
 */
@Data
@Table("mp_devops_load_scene_result_log")
public class MpDevopsLoadSceneLog extends MpDevopsLoad {

    /**
     * ID
     */
    private String resultLog;

    /**
     * 工具名称
     */
    private String resultStatus;

    /**
     * 工具描述
     */
    private String resultType;
}