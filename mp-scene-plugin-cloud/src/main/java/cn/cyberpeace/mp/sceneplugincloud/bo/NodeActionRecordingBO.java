package cn.cyberpeace.mp.sceneplugincloud.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(description = "场景关闭录屏数据")
@Data
public class NodeActionRecordingBO {
    @ApiModelProperty(value = "录屏名称")
    private String recordingName;
}
