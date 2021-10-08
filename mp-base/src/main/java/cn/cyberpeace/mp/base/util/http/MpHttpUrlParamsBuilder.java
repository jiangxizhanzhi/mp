package cn.cyberpeace.mp.base.util.http;

/**
 * @Description 构建 url params
 * @Author maojun
 * @Date 2021/9/9
 */
public class MpHttpUrlParamsBuilder {

    private static InputParser LINE_STR_PARSER = new LineStrParser();

    public static <T> PlainObjBuilder<T> builderForObj(T data) {
        return builderForObj(data, ParseMode.SNAKE);
    }

    public static <T> PlainObjBuilder<T> builderForObj(T data, ParseMode mode) {
        return new PlainObjBuilder(data, mode, LINE_STR_PARSER);
    }

    public static MapBuilder builderForMap() {
        return builderForMap(ParseMode.SNAKE);
    }

    public static MapBuilder builderForMap(ParseMode mode) {
        return new MapBuilder(mode, LINE_STR_PARSER);
    }
}
