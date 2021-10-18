package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetGuacamoleRecordingData implements CloudHttpData {
    private List<String> connectionID;
    private Integer page;
    private Integer limit;
    private String startTime;
    private String endTime;
    private String params;
}
