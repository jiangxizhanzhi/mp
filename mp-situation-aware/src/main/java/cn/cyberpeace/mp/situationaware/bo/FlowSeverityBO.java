package cn.cyberpeace.mp.situationaware.bo;

import lombok.*;

/**
 * 聚合攻击方式
 * @author wanggang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowSeverityBO extends BaseBO {

    /**
     * 警告等级ID
     */
    private String severityId;

}
