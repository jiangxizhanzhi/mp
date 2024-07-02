package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName ScreenRecordData
 * @Description
 * @Author shgang
 * @Date 2022/12/5
 * @Version 1.0.0
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScreenRecordData {

    private String Url;
    private boolean success;
    private String requestId;
}
