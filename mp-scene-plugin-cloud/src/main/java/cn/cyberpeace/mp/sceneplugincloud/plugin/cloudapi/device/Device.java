package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class Device {


    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private Integer id;

    /**
     * uuid
     */
    @ApiModelProperty(value = "UUID")
    private String uuid;

    @ApiModelProperty(value = "运维模型id")
    private String ywModelId;
    @ApiModelProperty(value = "运维设备id")
    private String ywId;

    //属性：NC
    @ApiModelProperty(value = "内存")
    private String memory;

    //属性：CPU
    @ApiModelProperty(value = "cpu核数")
    private String cpu;

    //属性：CP
    @ApiModelProperty(value = "磁盘")
    private String disk;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private String updatedAt;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记")
    private Boolean deleted;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createdAt;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    private String deletedAt;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "版本")
    private String deviceVersion;

    @ApiModelProperty(value = "父靶标名称")
    private String parentName;

    @ApiModelProperty(value = "资源状态")
    // 0-不可用, 1-可用, 2-占用（待定）
    private Integer status;

    /**
     * 镜像名称（靶标的英文名称）
     */
    @ApiModelProperty(value = "镜像名称（靶标的英文名称）")
    private String image;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 图标地址
     */
    @ApiModelProperty(value = "图标地址")
    private String logo;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String imageType;

    /**
     * 类型
     */
    @ApiModelProperty(value = "型号")
    private String deviceType;

    /**
     *
     */
    @ApiModelProperty(value = "靶标分类(TRM:1,TGM:2,CGM:3,其他:0)")
    private Integer maintype;

    /**
     * 角色
     */
    @ApiModelProperty(value = "角色")
    private Integer role;
    /**
     * 唯一标识靶标类型,兼容运维类型
     */
    private Integer roleId;

    /**
     * 一级分类
     */
    @ApiModelProperty(value = "一级分类")
    private Integer category;

    /**
     * 二级分类
     */
    @ApiModelProperty(value = "二级分类")
    private Integer secondCategory;

    /**
     * 三级分类
     */
    @ApiModelProperty(value = "三级分类")
    private Integer l3Category;

    /**
     * 是否导入
     */
    @ApiModelProperty(value = "是否导入")
    private Boolean isImport;

    /**
     * 是否默认靶标
     */
    @ApiModelProperty(value = "是否默认靶标")
    private Boolean isInternal;

    /**
     * 是否初始靶标
     */
    @ApiModelProperty(value = "是否初始靶标")
    private Boolean isInit;
    @ApiModelProperty(value = "启动优先级")
    private Integer defaultInit;

    /**
     * 实体信息
     */
    private ExtEntity extEntity;

    private String username;

    private String devicePath;

    /**
     * 交换机
     */
    private List<ExtEntityAccess> extEntityAccess;

    @ApiModelProperty(value = "管理口ip")
    private String managerIp;

    /**
     * 系统信息
     */
    private ExtSystemInfo extSystemInfo;

    /**
     * 网关信息
     */
    private ExtGateway extGateway;

    /**
     * 虚拟机信息
     */
    private ExtVm extVm;

    /**
     * 镜像信息
     */
    private ExtImageInfo extImageInfo;

    private ExtDocker extDocker;
    

    /**
     * 是否内置镜像
     */
    private Boolean isBuildIn;

    /**
     * 是否隐藏
     */
    private Boolean isHide = Boolean.FALSE;

    /**
     * 默认可运行的镜像名称
     */
    private String defaultLaunchImageName;

    @ApiModelProperty(value = "管理员用户密码")
    private String rootPassword;

    /**
     * 操作系统版本
     */
    @ApiModelProperty(value = "操作系统版本")
    private String osVersion;

    /**
     * 网卡数量
     */
    @ApiModelProperty(value = "网卡数量")
    private Integer netIfCnt;

    /**
     * 镜像位置
     */
    @ApiModelProperty(value = "镜像位置")
    private String imagePath;

    @ApiModelProperty(value = "是否支持用户行为")
    private Boolean behaveSupport;

    @ApiModelProperty(value = "靶标类型 0:普通镜像 1:内置镜像 2:临时镜像 3:原始镜像")
    private Integer deviceIdentify;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "镜像文件上传前的md5")
    private String imageMd5;

    @ApiModelProperty("内置应用")
    private String innerApps;

    @ApiModelProperty("resourceId")
    private String resourceId;

    @ApiModelProperty("模型ID")
    private String modelId;

    @ApiModelProperty("模型Code")
    private String modelCode;

    @ApiModelProperty("是否是攻防靶标模拟器")
    private Boolean attackMockDeviceFlag;

    public Boolean getIsBuildIn() {
        boolean tmpImport = !Objects.isNull(isImport) && isImport;
        boolean tmpInternal = !Objects.isNull(isInternal) && isInternal;
        boolean tmpInit = !Objects.isNull(isInit) && isInit;
        return tmpImport || tmpInternal || tmpInit;
    }

    private Boolean supportExport;
}
