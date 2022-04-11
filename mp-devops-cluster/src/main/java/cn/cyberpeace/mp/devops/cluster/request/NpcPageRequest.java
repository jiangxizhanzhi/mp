package cn.cyberpeace.mp.devops.cluster.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * NPC分页请求信息
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NpcPageRequest extends BasePageRequest implements Serializable {

}
