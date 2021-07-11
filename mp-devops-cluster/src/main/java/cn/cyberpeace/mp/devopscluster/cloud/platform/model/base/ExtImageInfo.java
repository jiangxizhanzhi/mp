package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("扩展镜像信息")
public class ExtImageInfo implements Serializable {

    @ApiModelProperty("UUID")
    private String uuid;

    
    @ApiModelProperty("基础镜像名称")
    private String baseImageName;

    
    @ApiModelProperty("是否支持基础镜像")
    private Boolean baseImageSupport;

    @ApiModelProperty("元数据")
    private ImageMetaData metaData;

    
    @ApiModelProperty("镜像状态")
    private Integer imageStatus;

    @ApiModelProperty("错误信息")
    private String error;

    
    @ApiModelProperty("系统镜像类型，1：基础镜像，2：CP-CLOUD已有镜像，3：自定义上传")
    private Integer imageSource;

    
    @ApiModelProperty("上传方式，http or ftp")
    private String uploadType;

    
    @ApiModelProperty("磁盘格式")
    private String diskFormat;


    @ApiModelProperty("临时文件ID")
    private String tmpdirUuid;

    @ApiModelProperty("ftp文件名称")
    private String ftpFilename;


    @ApiModelProperty("http文件名称")
    private String userFilename;

    @ApiModelProperty("正在执行操作")
    private String deviceActions;
}
