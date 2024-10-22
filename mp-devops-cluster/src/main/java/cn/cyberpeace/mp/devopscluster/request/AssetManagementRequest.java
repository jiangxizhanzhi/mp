package cn.cyberpeace.mp.devopscluster.request;

import lombok.Data;

@Data
public class AssetManagementRequest {

    private String id;
    //资产名称
    private String name;
    //资产地址
    private String address;
    //分值
    private String score;
    //资产描述
    private String description;
    //队伍或者人员id
    private String teamId;
    //相关联的场景ID
    private String sceneId;
}
