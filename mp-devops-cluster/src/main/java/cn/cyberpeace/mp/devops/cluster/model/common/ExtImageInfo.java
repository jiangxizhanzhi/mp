package cn.cyberpeace.mp.devops.cluster.model.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("镜像")
public class ExtImageInfo {

    @ApiModelProperty("UUID")
    private String uuid;

    /**
     * 基础镜像
     */
    @ApiModelProperty("基础镜像名称")
    private String baseImage;

    /**
     * 是否支持基础镜像
     */
    @ApiModelProperty("是否支持基础镜像")
    private Boolean baseImageSupport;

    @ApiModelProperty("元数据")
    private String metaData;

    /**
     * 0：待编辑，1：创建中，2：已创建，3：错误
     */
    @ApiModelProperty("镜像状态")
    private Integer imageStatus;

    @ApiModelProperty("错误信息")
    private String error;

    /**
     * 系统镜像类型，1：基础镜像，2：openstack已有镜像，3：自定义上传
     */
    @ApiModelProperty("系统镜像类型，1：基础镜像，2：openstack已有镜像，3：自定义上传")
    private Integer imageSource;

    /**
     * 上传方式
     */
    @ApiModelProperty("上传方式，http or ftp")
    private String uploadType;

    /**
     * ftp文件名称
     */
    @ApiModelProperty("ftp文件名称")
    private String ftpFilename;

    /**
     * 磁盘格式
     */
    @ApiModelProperty("磁盘格式")
    private String diskFormat;

    /**
     * HW磁盘格式
     */
    @ApiModelProperty("HW磁盘格式")
    private String hwDiskFormat;

    /**
     * VIF模型
     */
    @ApiModelProperty("VIF模型")
    private String hwVifModel;
}
