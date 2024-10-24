package cn.cyberpeace.mp.devopscluster.request;

import lombok.Data;

import java.util.List;

@Data
public class TaskManagementRequest {

    private String id;
    //任务名称
    private String name;
    //资产名称
    private List<String> assetsName;
    //资产id
    private List<String> assetsId;
    //红方人员或者队伍id
    private List<String> redId;
    //裁判id
    private List<String> refereeId;
    //相关联的场景ID
    private String sceneId;

    private String type;
}
