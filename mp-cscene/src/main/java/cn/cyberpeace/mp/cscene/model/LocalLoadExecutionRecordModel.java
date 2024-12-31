package cn.cyberpeace.mp.cscene.model;


import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 本地载荷执行记录表
 * @TableName mp_local_load_execution_record
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_local_load_execution_record")
public class LocalLoadExecutionRecordModel extends BaseModel {

    @ApiModelProperty(value = "场景id")
    private String scene;

    @ApiModelProperty(value = "ojj实例id")
    private String resourceId;

    @ApiModelProperty(value = "载荷节点")
    private String loadNode;

    @ApiModelProperty(value = "载荷节点名")
    private String loadNodeName;

    @ApiModelProperty(value = "源ip")
    private String sourceIp;

    @ApiModelProperty(value = "目标节点")
    private String destinationNode;

    @ApiModelProperty(value = "目标ip")
    private String destinationIp;

    @ApiModelProperty(value = "状态 0 未执行 1 已执行 2 执行失败 3 动效发送 4 无动效")
    private Integer status;

    public enum Status {
        NOT_EXECUTED(0),
        EXECUTED(1),
        EXECUTION_FAILED(2),
        ANIMATION_SENT(3),
        NO_ANIMATION(4);

        private final int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Status fromCode(int code) {
            for (Status status : Status.values()) {
                if (status.getCode() == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown status code: " + code);
        }
    }
}

