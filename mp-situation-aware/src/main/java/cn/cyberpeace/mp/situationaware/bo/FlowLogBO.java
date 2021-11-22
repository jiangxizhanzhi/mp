package cn.cyberpeace.mp.situationaware.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聚合攻击方式
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowLogBO {

    /**
     * 来源
     */
    private AttackSource source;

    /**
     * 目的地
     */
    private AttackDestination destination;

    /**
     * 网络
     */
    private AttackNetwork network;

    /**
     * suricata信息
     */
    private AttackSuricata suricata;

    /**
     * 创建时间
     */
    private Long timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class AttackSource{

        private String ip;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class AttackDestination{

        private String ip;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class AttackNetwork{

        private Long bytes;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class AttackSuricata{

        private List<String> category;

    }
}
