package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.bo.ProtocolBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: pcap解析返回体
 * @Author qiaokang
 * @Date：2022/11/1 15:23
 */
@Data
@ApiModel("pcap解析返回体")
public class PcapAnalyseResponse {

    /**
     * pcap文件fdfs地址
     */
    @ApiModelProperty(value = "pcap包fdfs地址")
    private String pcapFile;

    /**
     * pcap包含的协议
     */
    @ApiModelProperty(value = "pcap包含协议集合")
    private List<ProtocolBO> protocolList;


}
