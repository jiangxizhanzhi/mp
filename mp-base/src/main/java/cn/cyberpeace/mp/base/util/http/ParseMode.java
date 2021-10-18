package cn.cyberpeace.mp.base.util.http;

/**
 * @Description
 * @Author maojun
 * @Date 2021/9/9
 */
public enum ParseMode {
    /**
     * 保持key的格式,不做任何转换
     */
    KEEP,
    /**
     * 将key转成小写字母+下划线的写法, userName->user_name
     */
    SNAKE
}
