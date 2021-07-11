package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceVO implements Serializable {

    
    @ApiModelProperty("ID")
    private Integer id;

    
    @ApiModelProperty("uuid")
    private String uuid;

    
    @ApiModelProperty("更新时间")
    private String updatedAt;

    
    @ApiModelProperty("删除标记")
    private Boolean deleted;

    
    @ApiModelProperty("创建时间")
    private String createdAt;

    
    @ApiModelProperty("删除时间")
    private String deletedAt;

    
    @ApiModelProperty("名称")
    private String name;

    
    @ApiModelProperty("镜像名称")
    private String image;

    
    @ApiModelProperty("描述")
    private String description;

    
    @ApiModelProperty("图标地址")
    private String logo;

    
    @ApiModelProperty("类型")
    private String imageType;

    
    @ApiModelProperty("自定义类型")
    private Integer maintype;

    
    @ApiModelProperty("角色")
    private Integer role;

    
    @ApiModelProperty("一级分类")
    private Integer category;

    
    @ApiModelProperty("二级分类")
    private Integer secondCategory;

    
    @ApiModelProperty("三级分类")
    private Integer thirdCategory;

    
    @ApiModelProperty("是否导入")
    private Boolean isImport;

    
    @ApiModelProperty("是否默认靶标")
    private Boolean isInternal;

    
    @ApiModelProperty("实体信息")
    private ExtEntity extEntity;

    
    @ApiModelProperty("交换机信息")
    private List<ExtEntityAccess> extEntityAccess;

    
    @ApiModelProperty("系统信息")
    private ExtSystemInfo extSystemInfo;

    
    @ApiModelProperty("网关信息")
    private ExtGateway extGateway;

    
    @ApiModelProperty("虚拟机信息")
    private ExtVm extVm;

    
    @ApiModelProperty("镜像信息")
    private ExtImageInfo extImageInfo;

    @ApiModelProperty("容器信息")
    private ExtDocker extDocker;

    
    @ApiModelProperty("是否内置镜像")
    private Boolean isBuildIn;

    
    @ApiModelProperty("是否隐藏")
    private Boolean isHide;

    
    @ApiModelProperty("默认可运行的镜像名称")
    private String defaultLaunchImageName;

    public Boolean getIsBuildIn() {
        boolean tmpImport = !Objects.isNull(isImport) && isImport;
        boolean tmpInternal = !Objects.isNull(isInternal) && isInternal;
        return tmpImport || tmpInternal;
    }

    public String getImage() {
        
        return StringUtils.isBlank(image) ? name : image;
    }
}
