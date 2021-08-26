package cn.cyberpeace.mp.flow.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @Description: 结果解析工具
 * @Autho qiaokang
 * @Date：2021/8/10 17:46
 */
public class ResponseUtils {

    /**
     * 正常状态码
     */
    private static final String SUCCESS_CODE = "1000";

    private static final String STR_CODE = "code";

    /**
     * 校验返回结果码是否正常
     *
     * @param object
     * @return
     */
    public static boolean checkResponse(JSONObject object) {
        return Objects.nonNull(object) && StringUtils.isNotBlank(object.getString(STR_CODE))
                && SUCCESS_CODE.equals(object.getString(STR_CODE));
    }

}
