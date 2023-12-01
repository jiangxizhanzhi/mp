package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SceneElementPortBO extends BaseSceneElementPortBO<SceneElementPortModel, SceneElementPortBO, SceneElementBO> {

    @JsonIgnore
    private boolean using = true;

    private boolean change = false;

    private Object object = new Object();

    @Override
    public SceneElementPortModel createModel() {
        return new SceneElementPortModel();
    }

    @Override
    public void fromModel(SceneElementPortModel obj, String... ignoreFields) {
        // fromModel 和 toModel 得互斥,否则在并发下会出现 model 中 port_id = id 的情况
        // 因为 fromModel 里面id的值设置 , 分两步 , 第一步会把数据库id复制到bo的id,第二步执行 setId(obj.getPortId());
        // 第一步和第二步之间执行 toModel 就会有问题了
//        synchronized (object) {
            super.fromModel(obj, ignoreFields);
//        }
    }

    @Override
    public SceneElementPortModel toModel(String... ignoreFields) {
//        synchronized (object) {
            return super.toModel(ignoreFields);
//        }
    }

}
