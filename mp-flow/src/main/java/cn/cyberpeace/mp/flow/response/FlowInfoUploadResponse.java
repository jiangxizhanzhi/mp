package cn.cyberpeace.mp.flow.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 流量上传返回信息
 * @Autho qiaokang
 * @Date：2021/8/10 16:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量上传返回信息")
public class FlowInfoUploadResponse {

    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件类型
     */
    @ApiModelProperty(value = "文件类型")
    private String fileType;

    /**
     * 文件url
     */
    @ApiModelProperty(value = "文件url")
    private String fileUrl;

    /**
     * 第三方id
     */
    @ApiModelProperty(value = "第三方id")
    private String thirdId;

    /**
     * 第三方文件名称
     */
    @ApiModelProperty(value = "第三方文件名称")
    private String thirdName;

}
