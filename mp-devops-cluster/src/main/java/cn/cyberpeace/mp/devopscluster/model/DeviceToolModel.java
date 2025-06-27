package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*CREATE TABLE mp_devops_tool (
    id VARCHAR(32) NOT NULL COMMENT '靶标id',
    tool_name VARCHAR(128) NOT NULL COMMENT '工具名称',
    tool_params VARCHAR(4096) NOT NULL COMMENT '工具参数',
    attack_method VARCHAR(4096) NOT NULL COMMENT '攻击方式',
    return_type VARCHAR(128) NOT NULL COMMENT '攻击返回类型',
    PRIMARY KEY (id)
);*/
@Data
@AllArgsConstructor
@NoArgsConstructor
//@Table("mp_devops_tool")
public class DeviceToolModel extends BaseModel {

    /**
     * 靶标id
     */
    private String deviceUuid;

    /**
     * 工具名称
     */
    private String toolName;

    /**
     * 工具参数
     */
    private String toolParams;

    /**
     * 攻击方式
     */
    private String attackMethod;

    /**
     * 攻击返回类型
     */
    private List<String> returnType;

}
