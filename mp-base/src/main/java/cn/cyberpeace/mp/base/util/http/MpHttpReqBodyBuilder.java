package cn.cyberpeace.mp.base.util.http;

/**
 * @Description 构建 request body
 * @Author maojun
 * @Date 2021/9/9
 */
public class MpHttpReqBodyBuilder {
    public static InputParser JSON_PARSER = new JsonParser();

    public static <T> PlainObjBuilder<T> builderFor(T data) {
        return builderFor(data, ParseMode.SNAKE);
    }

    public static <T> PlainObjBuilder<T> builderFor(T data, ParseMode mode) {
        return new PlainObjBuilder<>(data, mode, JSON_PARSER);
    }

    public static MapBuilder builderForMap() {
        return builderForMap(ParseMode.SNAKE);
    }

    public static MapBuilder builderForMap(ParseMode mode) {
        return new MapBuilder(mode, JSON_PARSER);
    }

}
