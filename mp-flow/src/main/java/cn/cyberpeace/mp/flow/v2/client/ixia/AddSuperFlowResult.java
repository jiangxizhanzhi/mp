package cn.cyberpeace.mp.flow.v2.client.ixia;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/31
 */
@Getter
@Setter
public class AddSuperFlowResult extends IxiaResponse {
    private Long createdOn;
    private String author;
    private String weightType;
    private String createdBy;
    private String name;
    private List<AddSuperFlowResultInfo> superflow;
    private String contentType;

    @Getter
    @Setter
    private static class AddSuperFlowResultInfo {
        private Integer weight;
        private boolean generated;
        private Long seed;
        private String name;
        private String label;
    }
}
