package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetGuacamoleRecordingResult {
    private Integer totalnum;
    private Integer totalpage;
    private Boolean success;
    private String requestId;
    private List<Record> data;
}
