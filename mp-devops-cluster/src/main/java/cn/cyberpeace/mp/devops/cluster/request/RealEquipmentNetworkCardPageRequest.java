package cn.cyberpeace.mp.devops.cluster.request;


import cn.cyberpeace.mp.devops.cluster.model.common.SnmpConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RealEquipmentNetworkCardPageRequest extends BasePageRequest{

    private String uuid;
}
