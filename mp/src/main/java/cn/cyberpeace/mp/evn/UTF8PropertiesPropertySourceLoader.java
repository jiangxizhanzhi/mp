package cn.cyberpeace.mp.evn;


import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * 参见https://github.com/spring-projects/spring-boot/issues/28663
 * spring加载.property 只支持ISO_8859_1字符集，不支持中文字符,有三种修改方法
 * 1。改用yml格式
 * 2. .property文件内容涉及到ISO_8859_1字符集以外的内容，使用Unicode escapes形式
 * 3. 自定义一个.property loader
 *
 * 这边采用3
 *
 * @ClassName UTF8PropertiesPropertySourceLoader
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/1 16:25
 **/
public class UTF8PropertiesPropertySourceLoader implements PropertySourceLoader {
    private static final String XML_FILE_EXTENSION = ".xml";

    @Override
    public String[] getFileExtensions() {
        return new String[] { "properties", "xml" };
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        List<Map<String, ?>> properties = loadProperties(resource);
        if (properties.isEmpty()) {
            return Collections.emptyList();
        }
        List<PropertySource<?>> propertySources = new ArrayList<>(properties.size());
        for (int i = 0; i < properties.size(); i++) {
            String documentNumber = (properties.size() != 1) ? " (document #" + i + ")" : "";
            propertySources.add(new OriginTrackedMapPropertySource(name + documentNumber,
                    Collections.unmodifiableMap(properties.get(i)), true));
        }
        return propertySources;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Map<String, ?>> loadProperties(Resource resource) throws IOException {
        String filename = resource.getFilename();
        List<Map<String, ?>> result = new ArrayList<>();
        if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
            result.add((Map) PropertiesLoaderUtils.loadProperties(resource));
        }
        else {
            List<UTF8TrackedPropertiesLoader.Document> documents = new UTF8TrackedPropertiesLoader(resource).load();
            documents.forEach((document) -> result.add(document.asMap()));
        }
        return result;
    }

}
