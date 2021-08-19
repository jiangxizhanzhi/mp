package cn.cyberpeace.mp.cscene.bo.base;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import lombok.Data;

@Data
public abstract class BaseCsceneBO<M extends BaseCsceneModel> extends ModelBO<M> {
    private Boolean deleted;
}
