package cn.cyberpeace.mp.scene.core.bo.base;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName BaseSceneContextBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public abstract class BaseSceneContextBO {
    private Map<String, Object> plugins;
}
