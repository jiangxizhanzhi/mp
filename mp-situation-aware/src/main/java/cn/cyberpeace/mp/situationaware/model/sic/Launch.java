package cn.cyberpeace.mp.situationaware.model.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


@Data
public class Launch<O, I> {

    @JSONField(name = "sic_launch_group")
    private String group;

    @JSONField(name = "sic_launch_seconds")
    private String seconds;

    @JSONField(name = "sic_launch_returncode")
    private String returnCode;

    @JSONField(name = "sic_launch_status")
    private String status;

    @JSONField(name = "sic_launch_id")
    private String id;

    
    @JSONField(name = "sic_launch_time")
    private String time;

    @JSONField(name = "sic_launch_output")
    private O output;

    @JSONField(name = "sic_launch_input")
    private I input;

}
