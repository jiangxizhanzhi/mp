package cn.cyberpeace.mp.base.fdfsclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

/**
 * @Description: FastDFS返回对象
 * @Autho qiaokang
 * @Date：2021/6/21 15:01
 */
@Data
public class FileResponseData {

    /**
     * 返回状态编码
     */
    @JsonInclude(Include.NON_NULL)
    private String code;

    /**
     * 返回信息
     */
    @JsonInclude(Include.NON_NULL)
    private String message;

    /**
     * 成功标识
     */
    private boolean success = true;

    /**
     * 文件路径
     */
    @JsonInclude(Include.NON_NULL)
    private String filePath;

    /**
     * 文件名称
     */
    @JsonInclude(Include.NON_NULL)
    private String fileName;

    /**
     * 文件类型
     */
    @JsonInclude(Include.NON_NULL)
    private String fileType;

    /**
     * Http URL
     */
    @JsonInclude(Include.NON_NULL)
    private String httpUrl;

    /**
     * 内部地址
     */
    @JsonInclude(Include.NON_NULL)
    private String innerHttpUrl;

    /**
     * Http Token
     */
    @JsonInclude(Include.NON_NULL)
    private String token;


    public FileResponseData() {
    }

    public FileResponseData(boolean success) {
        this.success = success;
    }

}
