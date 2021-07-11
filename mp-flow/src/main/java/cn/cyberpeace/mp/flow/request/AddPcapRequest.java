package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "新增pcap管理请求")
public class AddPcapRequest {

    
    @ApiModelProperty("名称")
    private String fileName;

    
    @ApiModelProperty("描述")
    private String description;

    
    @ApiModelProperty("标签")
    private String tag;

    
    @ApiModelProperty("类型：strike 武器库，normal 普通流量")
    private String type = "normal";

    
    @ApiModelProperty("文件类别：DDOS、cve、fuzze")
    private String categoryId;

}
