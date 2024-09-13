package cn.cyberpeace.mp.devopscluster.po;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_task_node_target")
public class VulnTaskTargetPO extends BaseModel {

    /**
     * 报告ID
     */
    private String recordId;

    /**
     * 报告名称
     */
    private String recordName;

    /**
     * 节点
     */
    private String node;

    /**
     * 绑定镜像ID列表
     */
    private String images;

}
