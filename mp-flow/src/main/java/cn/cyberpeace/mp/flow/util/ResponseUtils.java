package cn.cyberpeace.mp.flow.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;


public class ResponseUtils {

    
    private static final String SUCCESS_CODE = "1000";

    private static final String STR_CODE = "code";

    
    private static final String RUNNING_CODE = "6008";

    
    public static boolean checkResponse(JSONObject object) {
        return Objects.nonNull(object) && StringUtils.isNotBlank(object.getString(STR_CODE))
                && SUCCESS_CODE.equals(object.getString(STR_CODE));
    }

    
    public static boolean checkRunning(JSONObject object) {
        return Objects.nonNull(object) && StringUtils.isNotBlank(object.getString(STR_CODE))
                && RUNNING_CODE.equals(object.getString(STR_CODE));
    }


}
