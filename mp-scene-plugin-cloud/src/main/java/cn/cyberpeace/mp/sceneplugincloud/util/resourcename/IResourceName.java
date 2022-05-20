package cn.cyberpeace.mp.sceneplugincloud.util.resourcename;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/18
 */
public interface IResourceName<T extends RsourceNameParams> {
    String generate(String id, RsourceNameParams params);

    ResourceParseWrapper<T> parse(String name);
}