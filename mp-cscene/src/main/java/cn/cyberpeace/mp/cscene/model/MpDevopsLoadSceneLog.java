package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 载荷执行结果
 *
 * @since 2024-03-28 16:41:10
 */
@Data
@NoArgsConstructor
@Table("mp_devops_load_scene_result_log")
public class MpDevopsLoadSceneLog extends MpDevopsLoad {

    /**
     * 工具实例id
     */
    private String instanceId;

    /**
     * 场景id
     */
    private String sceneId;

    /**
     * 任务id
     */
    private String taskId;

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

    /**
     * 研判结果(0:未研判 1:成功 2:失败)
     */
    private Integer judgmentStatus;

    /**
     * 备注 研判结果原因
     */
    private String remark;
    /**
     * 阶段id
     */
    private String stageId;

    /**
     * 阶段名称
     */
    private String stageName;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String finishTime;

    /**
     * 结果类型名称
     */
    private String resultTypeName;

    /**
     * 结果状态名称
     */
    private String resultStatusName;
}