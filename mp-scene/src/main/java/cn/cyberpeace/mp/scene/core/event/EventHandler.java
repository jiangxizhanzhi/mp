package cn.cyberpeace.mp.scene.core.event;

import lombok.Data;

@Data
public class EventHandler<M extends BaseEventModel> {

    private M target;

}
