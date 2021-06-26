package cn.cyberpeace.mp.devops.cluster.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HypervisorRequest extends BaseRequest {

    /**
     * 主机名/节点名称
     */
    @NotBlank
    private String hostname;

    /**
     * 操作：重启reboot、关机poweroff
     */
    @NotBlank
    private String action;
}
