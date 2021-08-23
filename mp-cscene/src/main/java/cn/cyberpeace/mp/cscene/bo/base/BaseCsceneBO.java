package cn.cyberpeace.mp.cscene.bo.base;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import lombok.Data;

import java.util.List;

@Data
public abstract class BaseCsceneBO<
        M extends BaseCsceneModel,
        R0 extends BaseCsceneRoleModel,
        R extends BaseCsceneRoleBO<R, R0>> extends ModelBO<M> {
    private String id;
    private List<R> roles;
}
