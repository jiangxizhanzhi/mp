package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 节点管理分页请求参数
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "节点管理分页请求参数")
public class HypervisorPageRequest extends BasePageRequest {

    private String[] s;
}
