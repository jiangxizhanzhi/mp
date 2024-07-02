package cn.cyberpeace.mp.sceneplugincloud.util.resourcename;

/**
 * @ClassName IResourceName
 * @Description
 * @Author maojun
 * @Date 2022/5/18
 * @Version 1.0.0
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