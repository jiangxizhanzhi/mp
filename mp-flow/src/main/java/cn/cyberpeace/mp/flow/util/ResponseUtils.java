package cn.cyberpeace.mp.flow.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @Description: 结果解析工具
 * @Author qiaokang
 * @Date：2021/8/10 17:46
 */
public class ResponseUtils {

    /**
     * 正常状态码
     */
    private static final String SUCCESS_CODE = "1000";

    private static final String STR_CODE = "code";

    /**
     * 任务运行中返回码
     */
    private static final String RUNNING_CODE = "6008";

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

    /**
     * 校验返回码是否是运行中
     * @param object
     * @return
     */
    public static boolean checkRunning(JSONObject object) {
        return Objects.nonNull(object) && StringUtils.isNotBlank(object.getString(STR_CODE))
                && RUNNING_CODE.equals(object.getString(STR_CODE));
    }


}
