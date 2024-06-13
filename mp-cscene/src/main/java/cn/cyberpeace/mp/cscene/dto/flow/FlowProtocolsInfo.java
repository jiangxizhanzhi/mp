package cn.cyberpeace.mp.cscene.dto.flow;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/1 19:04
 */
@Data
public class FlowProtocolsInfo {
    private List<FlowProtocol> flowOptions;

    @Data
    public static class FlowProtocol {
        private String label;
        private String value;
    }
}
