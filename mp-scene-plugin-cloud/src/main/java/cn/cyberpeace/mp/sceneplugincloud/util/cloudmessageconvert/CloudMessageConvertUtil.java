package cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/7/11
 */
@Getter
@Setter
@Slf4j
public class CloudMessageConvertUtil {
    public static final String configKey = "cloud_message_convert";

    private static List<Convert> converts = new ArrayList<>();

    public static void init(String config) {
        try {
            converts.clear();
            List<Convert> result = JSON.parseArray(config, Convert.class);
            converts.addAll(result);
        } catch (Exception e) {
            log.error("failed to init cloud message convert");
        }
    }

    public static String getCode(String target) {
        String formatMessage = target.toLowerCase();
        for (var convert : converts) {
            if (convert.hit(formatMessage)) {
                return convert.getCode();
            }
        }
        return null;
    }

    @Setter
    @Getter
    private static class Convert {
        private String code;
        private List<ConvertRule> rules;

        public boolean hit(String target) {
            for (ConvertRule rule : rules) {
                if (!rule.hit(target)) {
                    return false;
                }
            }
            return true;
        }
    }
}
