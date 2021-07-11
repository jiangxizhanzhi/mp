package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.bo.ProtocolBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("pcap解析返回体")
public class PcapAnalyseResponse {

    
    @ApiModelProperty(value = "pcap包fdfs地址")
    private String pcapFile;

    
    @ApiModelProperty(value = "pcap包含协议集合")
    private List<ProtocolBO> protocolList;


}
