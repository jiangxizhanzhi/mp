package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuacamoleLogoutResult {
    private String messages;
    private Boolean success;
    private String requestId;
}
