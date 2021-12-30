package cn.cyberpeace.mp.devops.cluster.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 节点管理分页请求参数
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HypervisorPageRequest extends BasePageRequest implements Serializable {

}
