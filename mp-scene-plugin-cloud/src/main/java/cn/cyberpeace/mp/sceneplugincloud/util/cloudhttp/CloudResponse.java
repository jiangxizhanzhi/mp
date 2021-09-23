package cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudResponse<T> {
    private T data;
    private String code;
    private String message;
    private String detail;
    private Integer timestamp;
    private Float timeElapse;
}
