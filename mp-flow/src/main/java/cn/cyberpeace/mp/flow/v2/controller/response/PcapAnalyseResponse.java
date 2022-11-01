package cn.cyberpeace.mp.flow.v2.controller.response;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author qiaokang
 * @Date：2022/11/1 15:23
 */
@Data
public class PcapAnalyseResponse {

    /**
     * pcap文件fdfs地址
     */
    private String pcapFile;

    /**
     * pcap包含的协议
     */
    private List<String> protocolList;

}
