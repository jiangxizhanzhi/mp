package cn.cyberpeace.mp.base.util.http;

/**
 * @Description
 * @Author maojun
 * @Date 2021/9/9
 */
public enum ParseMode {
    /**
     * 直接转换成json,不转换任何的key
     */
    JUST_JSON,
    /**
     * 转成json,将key转成小写字母+下划线的写法, userName->user_name
     */
    SNAKE_JSON
}
