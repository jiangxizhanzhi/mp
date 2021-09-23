package cn.cyberpeace.mp.scene.core.bo.base;

import lombok.Data;

import java.util.Map;

@Data
public abstract class BaseSceneContextBO {
    private Map<String, Object> plugins;
}
