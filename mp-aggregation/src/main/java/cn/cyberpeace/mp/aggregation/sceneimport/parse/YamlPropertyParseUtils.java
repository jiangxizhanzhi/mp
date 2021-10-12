package cn.cyberpeace.mp.aggregation.sceneimport.parse;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description yml文件中key名称格式化为驼峰
 * @Author maojun
 * @Date 2021/10/11
 */
class YamlPropertyParseUtils extends PropertyUtils {
    private static final Pattern linePattern = Pattern.compile("-([a-z])");

    @Override
    public Property getProperty(Class<?> type, String name) {
        if (name.contains("-")) {
            return super.getProperty(type, this.lineToHump(name));
        }
        return super.getProperty(type, name);
    }

    public String lineToHump(String str) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = linePattern.matcher(str);
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
