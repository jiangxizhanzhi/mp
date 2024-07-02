package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @ClassName GetGuacamoleRecordingResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
