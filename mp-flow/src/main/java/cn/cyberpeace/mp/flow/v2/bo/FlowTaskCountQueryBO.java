package cn.cyberpeace.mp.flow.v2.bo;

import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author maojun
 * @Date 2022/11/1
 */
@Data
public class FlowTaskCountQueryBO {
    private Date startTime;
    private Date endTime;
}
