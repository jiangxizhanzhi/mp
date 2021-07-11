package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;


class PrepareConverter implements IConverter {

    private YmlParseResult parseResult;

    public PrepareConverter(YmlParseResult parseResult) {
        this.parseResult = parseResult;
    }

    public void convert2BO(ConvertDataContainer container) {
        if (CollectionUtils.isNotEmpty(parseResult.getNetworks()) && CollectionUtils.isNotEmpty(parseResult.getNodes())) {
            Set<String> simpleSwitchIds = new HashSet<>();
            parseResult.getNetworks().forEach(n -> simpleSwitchIds.add("router-" + n.getId()));
            var it = parseResult.getNodes().iterator();
            while (it.hasNext()) {
                var node = it.next();
                if (simpleSwitchIds.contains(node.getId())) {
                    container.getSimpleLayer3Switch().add(StringUtils.replaceOnce(node.getId(), "router-", ""));
                    it.remove();
                }
            }
        }
    }

}
