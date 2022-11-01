package cn.cyberpeace.mp.flow.v2.client.ixia;

import lombok.Data;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/31
 */
@Data
public class CreateApplicationProfileResult extends IxiaResponse{
    private Long createdOn;
    private String author;
    private String weightType;
    private String createdBy;
    private String name;
    //todo:还需要一个类型,暂时不接
    //private String superflow;
    //"superflow": [],
    private String custom;
}
