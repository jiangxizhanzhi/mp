package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScreenRecordData {

    private String Url;
    private boolean success;
    private String requestId;
}
