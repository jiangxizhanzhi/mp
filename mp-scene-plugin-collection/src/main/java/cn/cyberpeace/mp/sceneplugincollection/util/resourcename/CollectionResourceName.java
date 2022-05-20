package cn.cyberpeace.mp.sceneplugincollection.util.resourcename;

import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.ResourceParseWrapper;
import cn.cyberpeace.mp.sceneplugincloud.util.resourcename.RsourceNameParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/20
 */
@Component
public class CollectionResourceName implements ICollectionResourceName<RsourceNameParams> {
    @Override
    public String generate(String id, RsourceNameParams params) {
        return this.getPrefix() + id;
    }

    @Override
    public ResourceParseWrapper<RsourceNameParams> parse(String name) {
        if (StringUtils.isNotEmpty(name) && name.startsWith(this.getPrefix())) {
            String sceneId = StringUtils.replace(name, this.getPrefix(), "");
            RsourceNameParams params = new RsourceNameParams();
            params.setSceneId(sceneId);
            return new ResourceParseWrapper<>(params);
        }
        return new ResourceParseWrapper<>();
    }

    private String getPrefix() {
        return "mp_arkime_";
    }
}
