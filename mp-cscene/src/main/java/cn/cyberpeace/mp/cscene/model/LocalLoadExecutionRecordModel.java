package cn.cyberpeace.mp.cscene.model;


import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
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
    /**
     * 场景
     */
    private String scene;

    /**
     * 流量索引
     */
    private String trafficIndex;

    /**
     * 载荷节点
     */
    private String loadNode;

    /**
     * 源ip
     */
    private String sourceIp;

    /**
     * 目标ip
     */
    private String destinationIp;

    /**
     * 状态
     */
    private Integer status;


}