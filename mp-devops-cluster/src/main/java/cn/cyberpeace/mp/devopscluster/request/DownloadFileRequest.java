package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DownloadFileRequest extends BasePageRequest implements Serializable {

    @ApiModelProperty(value = "协议")
    private String protocol;

    @ApiModelProperty(value = "场景id")
    private String sceneId;

    @ApiModelProperty(value = "文件路径")
    private String filePath;
}
