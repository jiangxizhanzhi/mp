package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.model.common.ExtGateway;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ImageConvertModel extends BasePageConvertModel{

    /**
     * UUID
     */
    private String uuid;

    /**
     * 名称
     */
    private String name;

    /**
     * 分类
     */
    private Integer mainCategory;

    /**
     * 子分类
     */
    private Integer subCategory;

    /**
     * 系统信息
     */
    private ExtSystemInfo extSystemInfo;

    /**
     * 描述
     */
    private String description;

    /**
     * 网关信息
     */
    private ExtGateway extGateway;

}
