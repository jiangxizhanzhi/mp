 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PcapAnalyseResponse.java.
 // File size 714.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.bo.ProtocolBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName PcapAnalyseResponse
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
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
