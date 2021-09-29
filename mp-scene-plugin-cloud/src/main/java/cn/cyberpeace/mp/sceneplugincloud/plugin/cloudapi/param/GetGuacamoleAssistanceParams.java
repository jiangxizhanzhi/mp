package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetGuacamoleAssistanceParams implements HttpParam {
    private String primaryconnectionID;
}
