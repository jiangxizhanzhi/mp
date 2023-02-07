package cn.cyberpeace.mp.sceneplugincollection.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class EsHttpUtil {

    private static HttpClient client = null;

    public static void updateDataMaxQueryNum(String indexPrefix, int total) {
        if (client == null) {
            synchronized (EsHttpUtil.class) {
                if (client == null) {
                    SituationAwareConfig config = BeanCommon.getBean(SituationAwareConfig.class);
                    String protocol = StringUtils.isEmpty(config.getArkimeEsProtocol()) ? "http" : config.getArkimeEsProtocol();
                    client = HttpClient.builder().configServer(protocol + "://" + config.getArkimeEsHost() + ":" + config.getArkimeEsPort()).build();
                }
            }
        }
        IndexParams indexParams = new IndexParams();
        indexParams.setMaxResultWindow(total < 1000000 ? 1000000 : total);
        Params params = new Params();
        params.setIndex(indexParams);
        client.put("/" + indexPrefix + "_sessions3-*/_settings", params, null, new HashMap<>(), null);
    }

    @Getter
    @Setter
    private static class Params implements SnakeHttpData {
        private IndexParams index;
    }

    @Getter
    @Setter
    private static class IndexParams {
        private Integer maxResultWindow;
    }
}
