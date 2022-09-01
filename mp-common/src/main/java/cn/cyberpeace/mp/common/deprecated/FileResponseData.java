package cn.cyberpeace.mp.common.deprecated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Deprecated
 * @Description: FastDFS返回对象
 * @Author qiaokang
 * @Date：2021/6/21 15:01
 */
@Data
public class FileResponseData {

    /**
     * 返回状态编码
     */
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "编码")
    private String code;

    /**
     * 返回信息
     */
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "信息")
    private String message;

    /**
     * 成功标识
     */
    @ApiModelProperty(value = "成功与否")
    private boolean success = true;

    /**
     * 文件路径
     */
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    /**
     * 文件名称
     */
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件类型
     */
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件类型")
    private String fileType;

    /**
     * Http URL
     */
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件下载url")
    private String httpUrl;

    /**
     * 内部地址
     */
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件下载内部url")
    private String innerHttpUrl;

    /**
     * Http Token
     */
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "token")
    private String token;

    /**
     * 文件大小
     */
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件大小")
    private Long fileSize;


    public FileResponseData() {
    }

    public FileResponseData(boolean success) {
        this.success = success;
    }

}
