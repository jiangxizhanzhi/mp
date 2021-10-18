package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 镜像上传请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagesUploadRequest extends BaseRequest {

    /**
     * 上传方式
     */
    @ApiModelProperty("上传方式")
    private String uploadType;

    /**
     * ftp文件名称
     */
    @ApiModelProperty("ftp文件名称")
    private String ftpFilename;

    /**
     * 类型：容器/虚拟机
     */
    @ApiModelProperty("类型：容器/虚拟机")
    private String type;

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
