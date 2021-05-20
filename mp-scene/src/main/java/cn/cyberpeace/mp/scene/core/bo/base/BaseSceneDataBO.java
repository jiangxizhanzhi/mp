package cn.cyberpeace.mp.scene.core.bo.base;

import lombok.Data;

@Data
public abstract class BaseSceneDataBO<T> {
    private T topology;
}
