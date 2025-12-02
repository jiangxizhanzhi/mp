package cn.cyberpeace.mp.devopscluster.vo;

import lombok.Data;

@Data
public class XdVirVO {

    //名称
    private String name;
    //id
    private String id;
    //ip地址
    private String addr;
    //状态 SHUTOFF：关机 ACTIVE：可用
    private String status;
    //状态描述
    private String statusDisplay;
    //镜像id
    private String imageId;
    //镜像名称
    private String imageName;
    //厂商 1华网创新 2深信服 3信大网域
    private String company;
    //厂商名称
    private String company_name;

}
