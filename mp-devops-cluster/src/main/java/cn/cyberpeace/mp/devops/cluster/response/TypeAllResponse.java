package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.AccessProtocolsPort;
import cn.cyberpeace.mp.devops.cluster.converter.model.SwitchConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeIdIntegerConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeIdStringConvertModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeAllResponse {

    /**
     * 一级类型
     */
    @ApiModelProperty("一级分类")
    private List<TypeConvertModel> firstTypeList;

    /**
     * 二级类型
     */
    @ApiModelProperty("二级分类")
    private List<TypeConvertModel> secondTypeList;

    /**
     * 三级类型
     */
    @ApiModelProperty("三级分类")
    private List<TypeConvertModel> thirdTypeList;

    /**
     * 角色
     */
    @ApiModelProperty("镜像角色")
    private List<TypeIdIntegerConvertModel> role;

    /**
     * 角色
     */
    @ApiModelProperty("实体角色")
    private List<TypeIdIntegerConvertModel> entityRole;

    /**
     * 分类
     */
    @ApiModelProperty("分类")
    private List<TypeIdIntegerConvertModel> category;

    /**
     * 部署类型
     */
    @ApiModelProperty("部署类型")
    private List<TypeIdIntegerConvertModel> deployType;

    /**
     * 安装模式
     */
    @ApiModelProperty("安装模式")
    private List<TypeIdIntegerConvertModel> installMode;

    /**
     * 镜像类型
     */
    @ApiModelProperty("靶标类型")
    private List<TypeIdStringConvertModel> imageType;

    /**
     * 语言
     */
    @ApiModelProperty("语言")
    private List<TypeIdStringConvertModel> language;

    /**
     * 系统架构
     */
    @ApiModelProperty("系统架构")
    private List<TypeIdStringConvertModel> systemArchitecure;

    /**
     * 磁盘
     */
    @ApiModelProperty("磁盘格式")
    private List<TypeIdStringConvertModel> diskFormat;

    /**
     * 访问方式
     */
    @ApiModelProperty("访问方式")
    private List<TypeIdStringConvertModel> accessType;

    /**
     * 服务类别
     */
    @ApiModelProperty("服务类别")
    private List<TypeIdStringConvertModel> serviceCategories;

    /**
     * 服务启动方式
     */
    @ApiModelProperty("服务启动方式")
    private List<TypeIdStringConvertModel> serviceStartTypes;

    /**
     * 安装平台
     */
    @ApiModelProperty("安装平台")
    private List<TypeIdStringConvertModel> installerPlatforms;

    /**
     * 接入协议和端口
     */
    @ApiModelProperty("接入协议和端口")
    private List<AccessProtocolsPort> accessProtocolsPorts;

    /**
     * 交换机端口
     */
    @ApiModelProperty("交换机端口")
    private List<SwitchConvertModel> switchConvertModels;

    /**
     * 串口信息
     */
    @ApiModelProperty("串口信息")
    private List<SerialPortResponse> serialPortResponses;

    @ApiModelProperty("SNMP版本")
    private List<TypeIdStringConvertModel> snmpVersions;

    @ApiModelProperty("磁盘控制器类型")
    private List<String> hwDiskBusList;

    @ApiModelProperty("虚拟网卡型号")
    private List<String> hwVifModelList;

    @ApiModelProperty("视频图像驱动")
    private List<String> hwVideoModelList;

    public TypeAllResponse(List<TypeConvertModel> firstTypeList, List<TypeConvertModel> secondTypeList, List<TypeConvertModel> thirdTypeList) {
        this.firstTypeList = firstTypeList;
        this.secondTypeList = secondTypeList;
        this.thirdTypeList = thirdTypeList;
    }
}
