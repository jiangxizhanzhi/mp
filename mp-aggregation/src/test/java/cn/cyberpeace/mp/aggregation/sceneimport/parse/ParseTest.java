 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ParseTest.java.
 // File size 613.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;

/**
 * @Description
 * @Author maojun
 * @Date 2021/11/2
 */
public class ParseTest {
    public static void main(String[] args) {
        String path = ParseTest.class.getClassLoader().getResource("ymlparse/servers.yml").getPath();
        FilePathYmlParser<ImageParseResult> parser = new FilePathYmlParser<>(path, ImageParseResult.class);
        ImageParseResult result = parser.parse();
        System.out.println(result.getServers().size());
    }
}