package cn.cyberpeace.mp.devops.cluster.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HypervisorWeightAndMaxInstanceRequest extends BaseRequest {

    /**
     * 主机名/节点名称
     */
   private String hostname;
}
