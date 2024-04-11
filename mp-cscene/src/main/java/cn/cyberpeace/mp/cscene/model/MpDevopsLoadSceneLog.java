package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 载荷同步表(MpDevopsLoad)实体类
 *
 * @since 2024-03-28 16:41:10
 */
@Data
@NoArgsConstructor
@Table("mp_devops_load_scene_result_log")
public class MpDevopsLoadSceneLog extends MpDevopsLoad {

    /**
     * 结果日志
     */
    private String resultLog;

    /**
     * 结果状态
     */
    private String resultStatus;

    /**
     * 结果类型
     */
    private String resultType;
}