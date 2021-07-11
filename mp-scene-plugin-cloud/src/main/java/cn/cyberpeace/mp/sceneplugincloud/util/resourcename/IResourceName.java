package cn.cyberpeace.mp.sceneplugincloud.util.resourcename;


public interface IResourceName<T extends RsourceNameParams> {
    String generate(String id, RsourceNameParams params);

    ResourceParseWrapper<T> parse(String name);
}