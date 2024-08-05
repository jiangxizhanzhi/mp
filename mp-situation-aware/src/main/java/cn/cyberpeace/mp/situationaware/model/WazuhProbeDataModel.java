package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_wazuh_probe_data")
public class WazuhProbeDataModel extends BaseModel {

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("主机名称")
    private String nodeName;

    @ApiModelProperty("探针名称")
    private String probeName;

    @ApiModelProperty("底层信息")
    private String content;

    @ApiModelProperty("详情")
    private String eventDesc;

    @ApiModelProperty("时间")
    private LocalDateTime time;
}
