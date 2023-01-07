package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowPcapFileInfo {
    @ApiModelProperty("文件下载地址")
    private String downloadUrl ;
}
