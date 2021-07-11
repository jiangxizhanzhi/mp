package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device extends BaseCloudPlatformResult {

    
    @ApiModelProperty(value = "ID")
    private Integer id;

    
    @ApiModelProperty(value = "UUID")
    private String uuid;

    
    @ApiModelProperty(value = "更新时间")
    private String updatedAt;

    
    @ApiModelProperty(value = "删除标记")
    private Boolean deleted;

    
    @ApiModelProperty(value = "创建时间")
    private String createdAt;

    
    @ApiModelProperty(value = "删除时间")
    private String deletedAt;

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "镜像名称（靶标的英文名称）")
    private String image;

    
    @ApiModelProperty(value = "描述")
    private String description;

    
    @ApiModelProperty(value = "图标地址")
    private String logo;

    
    @ApiModelProperty(value = "类型")
    private String imageType;

    
    @ApiModelProperty(value = "靶标分类(TRM:1,TGM:2,CGM:3,其他:0)")
    private Integer maintype;

    
    @ApiModelProperty(value = "角色")
    private Integer role;

    
    @ApiModelProperty(value = "一级分类")
    private Integer category;

    
    @ApiModelProperty(value = "二级分类")
    private Integer secondCategory;

    
    @ApiModelProperty(value = "三级分类")
    private Integer l3Category;

    
    @ApiModelProperty(value = "是否导入")
    private Boolean isImport;

    
    @ApiModelProperty(value = "是否默认靶标")
    private Boolean isInternal;

    
    @ApiModelProperty(value = "是否初始靶标")
    private Boolean isInit;

    
    private ExtEntity extEntity;

    
    private List<ExtEntityAccess> extEntityAccess;

    
    private ExtSystemInfo extSystemInfo;

    
    private ExtGateway extGateway;

    
    private ExtVm extVm;

    
    private ExtImageInfo extImageInfo;

    private ExtDocker extDocker;

    
    private EntityUsb extEntityUsb;

    
    private Boolean isBuildIn;

    
    private Boolean isHide = Boolean.FALSE;

    
    private String defaultLaunchImageName;

    public Boolean getIsBuildIn() {
        boolean tmpImport = !Objects.isNull(isImport) && isImport;
        boolean tmpInternal = !Objects.isNull(isInternal) && isInternal;
        boolean tmpInit = !Objects.isNull(isInit) && isInit;
        return tmpImport || tmpInternal || tmpInit;
    }

    private Boolean supportExport;
}
