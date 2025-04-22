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
     * 载荷执行记录id
     */
    private String executionRecordId;

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

    /**
     * 命令
     */
    private String command;

//    建一个枚举 /**
//     * 研判结果(0:未研判 1:成功 2:失败)
//     */
    public enum JudgmentStatus {
        /**
         * 未研判
         */
        UNJUDGED(0, "未研判"),
        /**
         * 成功
         */
        SUCCESS(1, "成功"),
        /**
         * 失败
         */
        FAILURE(2, "失败");

        private final Integer code;

        private final String name;

        JudgmentStatus(Integer value, String name) {
            this.code = value;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

//        根据code 获取name
        public static String getName(Integer code) {
            for (JudgmentStatus status : JudgmentStatus.values()) {
                if (status.getCode().equals(code)) {
                    return status.getName();
                }
            }
            return null;
        }
    }
}