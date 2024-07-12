 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name IResourceName.java.
 // File size 527.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
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