package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @ClassName GetGuacamoleAssistanceData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetGuacamoleAssistanceData implements HttpData {
    private List<String> primaryconnectionID;
}
