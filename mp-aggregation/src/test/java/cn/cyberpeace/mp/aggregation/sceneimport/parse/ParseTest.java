package cn.cyberpeace.mp.aggregation.sceneimport.parse;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageParseResult;


public class ParseTest {
    public static void main(String[] args) {
        String path = ParseTest.class.getClassLoader().getResource("ymlparse/servers.yml").getPath();
        FilePathYmlParser<ImageParseResult> parser = new FilePathYmlParser<>(path, ImageParseResult.class);
        ImageParseResult result = parser.parse();
        System.out.println(result.getServers().size());
    }
}