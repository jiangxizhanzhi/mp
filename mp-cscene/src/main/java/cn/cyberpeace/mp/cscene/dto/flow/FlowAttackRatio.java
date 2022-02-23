package cn.cyberpeace.mp.cscene.dto.flow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.client.license.LicensesStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowAttackRatio {

    /**
     * 攻击次数
     */
    private Long totalAttackNumber;

    /**
     * 流量大小，单位B
     */
    private Double totalTrafficSize;

}