package cn.cyberpeace.mp.sceneplugincloud.util.resourcename;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/18
 */
public interface IResourceName<T extends RsourceNameParams> {
    /**
     * generate
     *
     * @param id
     * @param params
     * @return
     */
    String generate(String id, RsourceNameParams params);

    /**
     * parse
     *
     * @param name
     * @return
     */
    ResourceParseWrapper<T> parse(String name);
}