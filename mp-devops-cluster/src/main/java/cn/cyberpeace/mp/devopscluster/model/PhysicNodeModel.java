package cn.cyberpeace.mp.devopscluster.model;


import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;

@Table("mp_devops_physic_node")
@Data
public class PhysicNodeModel extends BaseModel {

    private String nodeName;
    private String os;
    private String rackPosition;
    private String cpuInfo;
    private String memoryInfo;
    private String diskInfo;
    private String nicInfo;
    private String connectionUrl;
}
