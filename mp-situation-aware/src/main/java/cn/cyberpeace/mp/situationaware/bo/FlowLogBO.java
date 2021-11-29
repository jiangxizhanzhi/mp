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

        /**
         * 源IP
         */
        private String ip;

        /**
         * mac地址
         */
        private List<String> mac;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class AttackDestination{

        /**
         * 目标IP
         */
        private String ip;

        /**
         * mac地址
         */
        private List<String> mac;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class AttackNetwork{

        /**
         * 流量大小
         */
        private Long bytes;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class AttackSuricata{

        /**
         * 分类
         */
        private List<String> category;

        /**
         * 分类名称
         */
        private List<String> categoryZh;

        /**
         * 严重性
         */
        private List<Integer> severity;

    }
}
