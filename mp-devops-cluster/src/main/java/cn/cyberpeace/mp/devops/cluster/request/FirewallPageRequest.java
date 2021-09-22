package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 防火墙分页请求信息
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FirewallPageRequest extends BasePageRequest implements Serializable {

}
