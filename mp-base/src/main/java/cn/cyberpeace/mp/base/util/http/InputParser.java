package cn.cyberpeace.mp.base.util.http;

import java.util.Map;

/**
 * @Description 将业务参数转换成 http请求中可用的字符串(比如json格式、url请求参数格式)
 * @Author maojun
 * @Date 2021/9/9
 */
interface InputParser {
    String parse(Map<String, Object> values, ParseMode mode);
}
