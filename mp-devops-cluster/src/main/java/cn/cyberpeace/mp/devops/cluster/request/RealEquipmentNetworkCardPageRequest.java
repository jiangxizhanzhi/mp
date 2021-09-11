package cn.cyberpeace.mp.devops.cluster.request;


import cn.cyberpeace.mp.devops.cluster.model.common.SnmpConfig;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "实装-接口资源分页请求信息")
public class RealEquipmentNetworkCardPageRequest extends BasePageRequest{

    private Integer version;

    private String countOid;

    private String oid;

    private SnmpConfig snmpConfig;
}
