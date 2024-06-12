package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FlowMirrorCreateRes {
    private Integer optionKey;
    private String flowMirrorId;
}
