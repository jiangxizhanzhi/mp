package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.AccessProtocolsPort;
import cn.cyberpeace.mp.devopscluster.converter.model.SwitchConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeIdIntegerConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeIdStringConvertModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeAllVO {

    
    @ApiModelProperty("一级分类")
    private List<TypeConvertModel> firstTypeList;

    
    @ApiModelProperty("二级分类")
    private List<TypeConvertModel> secondTypeList;

    
    @ApiModelProperty("三级分类")
    private List<TypeConvertModel> thirdTypeList;

    
    @ApiModelProperty("镜像角色")
    private List<TypeIdIntegerConvertModel> role;

    
    @ApiModelProperty("实体角色")
    private List<TypeIdIntegerConvertModel> entityRole;

    
    @ApiModelProperty("分类")
    private List<TypeIdIntegerConvertModel> category;

    
    @ApiModelProperty("部署类型")
    private List<TypeIdIntegerConvertModel> deployType;

    
    @ApiModelProperty("安装模式")
    private List<TypeIdIntegerConvertModel> installMode;

    
    @ApiModelProperty("靶标类型")
    private List<TypeIdStringConvertModel> imageType;

    
    @ApiModelProperty("语言")
    private List<TypeIdStringConvertModel> language;

    
    @ApiModelProperty("系统架构")
    private List<TypeIdStringConvertModel> systemArchitecure;

    
    @ApiModelProperty("磁盘格式")
    private List<TypeIdStringConvertModel> diskFormat;

    
    @ApiModelProperty("访问方式")
    private List<TypeIdStringConvertModel> accessType;

    
    @ApiModelProperty("服务类别")
    private List<TypeIdStringConvertModel> serviceCategories;

    
    @ApiModelProperty("服务启动方式")
    private List<TypeIdStringConvertModel> serviceStartTypes;

    
    @ApiModelProperty("安装平台")
    private List<TypeIdStringConvertModel> installerPlatforms;

    
    @ApiModelProperty("接入协议和端口")
    private List<AccessProtocolsPort> accessProtocolsPorts;

    
    @ApiModelProperty("交换机端口")
    private List<SwitchConvertModel> switchConvertModels;

    
    @ApiModelProperty("串口信息")
    private List<SerialPortVO> serialPortRespons;

    @ApiModelProperty("SNMP版本")
    private List<TypeIdStringConvertModel> snmpVersions;

    @ApiModelProperty("磁盘控制器类型")
    private List<String> hwDiskBusList;

    @ApiModelProperty("虚拟网卡型号")
    private List<String> hwVifModelList;

    @ApiModelProperty("视频图像驱动")
    private List<String> hwVideoModelList;

    public TypeAllVO(List<TypeConvertModel> firstTypeList, List<TypeConvertModel> secondTypeList, List<TypeConvertModel> thirdTypeList) {
        this.firstTypeList = firstTypeList;
        this.secondTypeList = secondTypeList;
        this.thirdTypeList = thirdTypeList;
    }
}
