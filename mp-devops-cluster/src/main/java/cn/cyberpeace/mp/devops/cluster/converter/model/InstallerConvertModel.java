package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.model.common.ExtDocker;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtNormal;
import cn.cyberpeace.mp.devops.cluster.model.common.Resource;
import cn.cyberpeace.mp.devops.cluster.model.common.StartUpParameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InstallerConvertModel extends BasePageConvertModel{

    /**
     * UUID
     */
    private String uuid;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标
     */
    private String logo;

    /**
     * 分类
     */
    private String categories;

    /**
     * 服务类型：windows服务/docker服务
     */
    private Integer deployType;

    /**
     * 对应的docker服务的扩展信息
     */
    private ExtDocker extDocker;

    /**
     * 启动参数（包装对象）
     */
    private List<StartUpParameter> startUpParameterList;

    /**
     * 对应的windows服务扩展信息
     */
    private ExtNormal extNormal;


    /**
     * 资源列表
     */
    private List<Resource> resourceList = new ArrayList<>();

}
