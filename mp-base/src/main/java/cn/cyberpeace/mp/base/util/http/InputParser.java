package cn.cyberpeace.mp.base.util.http;

import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/9/9
 */
interface InputParser {
    String parse(Map<String, Object> values, ParseMode mode);
}
