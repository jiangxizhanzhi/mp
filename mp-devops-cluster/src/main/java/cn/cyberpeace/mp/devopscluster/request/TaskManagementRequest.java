package cn.cyberpeace.mp.devopscluster.request;

import lombok.Data;

@Data
public class TaskManagementRequest {

    private String id;
    //任务名称
    private String name;
    //资产id
    private String assetsName;
    //资产id
    private String assetsId;
    //红方人员或者队伍id
    private String redId;
    //裁判id
    private String refereeId;
    //相关联的场景ID
    private String sceneId;
}
