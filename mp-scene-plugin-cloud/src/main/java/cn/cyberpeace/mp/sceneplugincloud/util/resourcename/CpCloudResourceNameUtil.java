package cn.cyberpeace.mp.sceneplugincloud.util.resourcename;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.hutool.core.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


@Component
public class CpCloudResourceNameUtil implements ICloudResourceName<RsourceNameParams> {
    @Override
    public String generate(String id, RsourceNameParams params) {
        if (params == null || params.getIndex() == null) {
            return StringUtils.joinWith("_", this.getPrefix(), id);
        } else {
            return StringUtils.joinWith("_", this.getPrefix(), id, params.getIndex());
        }
    }

    @Override
    public ResourceParseWrapper<RsourceNameParams> parse(String name) {
        if (StringUtils.isEmpty(name) || !name.startsWith(this.getPrefix())) {
            return new ResourceParseWrapper<>();
        }
        if (name.endsWith("collection")) {
            return new ResourceParseWrapper<>();
        }
        String sceneInfo = name.replace(this.getPrefix() + "_", "");
        String[] sp = sceneInfo.split("_");
        if (sp.length == 0) {
            return new ResourceParseWrapper<>();
        }
        var result = new RsourceNameParams();
        result.setNodeId(sp[0]);
        if (sp.length > 1) {
            if (NumberUtil.isNumber(sp[1])) {
                result.setIndex(Integer.parseInt(sp[1]));
            }
        }
        return new ResourceParseWrapper<>(result);
    }

    private String getPrefix() {
        return CloudSingleton.cloudAppConfig.getCpcloudResourceGroup();
    }
}
