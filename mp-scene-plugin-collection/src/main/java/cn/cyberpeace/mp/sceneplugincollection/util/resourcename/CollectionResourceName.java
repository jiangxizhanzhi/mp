package cn.cyberpeace.mp.sceneplugincollection.util.resourcename;

import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.ResourceParseWrapper;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.RsourceNameParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName CollectionResourceName
 * @Description
 * @Author maojun
 * @Date 2022/5/20
 * @Version 1.0.0
 * @see ICollectionResourceName
 */
// 删除arkime时对arkime的name有依赖,先不使用
@Component
public class CollectionResourceName implements ICollectionResourceName<RsourceNameParams> {
    /**
     * generate
     *
     * @param id
     * @param params
     * @return
     */
    @Override
    public String generate(String id, RsourceNameParams params) {
        return this.getPrefix() + id;
    }

    /**
     * parse
     *
     * @param name
     * @return
     */
    @Override
    public ResourceParseWrapper<RsourceNameParams> parse(String name) {
//        if (StringUtils.isNotEmpty(name) && name.startsWith(this.getPrefix())) {
//            String sceneId = StringUtils.replace(name, this.getPrefix(), "");
//            RsourceNameParams params = new RsourceNameParams();
//            params.setSceneId(sceneId);
//            return new ResourceParseWrapper<>(params);
//        }
//        return new ResourceParseWrapper<>();
        if (StringUtils.isNotEmpty(name) && name.endsWith("collection")) {
            String sceneId = StringUtils.replace(name, "_collection", "");
            sceneId = StringUtils.replace(sceneId, "MP_SCENE_", "");
            RsourceNameParams params = new RsourceNameParams();
            params.setSceneId(sceneId);
            return new ResourceParseWrapper<>(params);
        }
        return new ResourceParseWrapper<>();
    }

    /**
     * getPrefix
     *
     * @return
     */
    private String getPrefix() {
        return "mp_arkime_";
    }
}
