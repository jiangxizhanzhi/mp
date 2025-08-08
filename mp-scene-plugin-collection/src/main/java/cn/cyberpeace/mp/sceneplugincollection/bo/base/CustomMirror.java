package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import lombok.Data;

import java.util.Set;

@Data
public class CustomMirror {
    private Set<String> sourceNodes;

    private String targetNode;
}
