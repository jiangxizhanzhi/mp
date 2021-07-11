package cn.cyberpeace.mp.situationaware.model;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkTrafficMirrorRequest {
    
    @Builder.Default
    private List<String> vmInstanceIds = Lists.newArrayList();

    
    @Builder.Default
    private List<String> containerInstanceIds = Lists.newArrayList();

    
    @Builder.Default
    private List<String> arkimeInstanceNames = Lists.newArrayList();

    
    private String sceneId;

    private Boolean entity;
}
