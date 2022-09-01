package cn.cyberpeace.mp.base.common.textresource.base;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.textresource.converter.TextResourceConverter;
import cn.cyberpeace.mp.base.common.textresource.stereotype.TextResourceColumn;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TextResourceBase {

    private TextResourceBase() {}

    private static final Map<Class<?>, Map<String, TextResourceColumn>> beanClassFieldColumnAnnotationMapCache = new ConcurrentHashMap<>();

    public static Map<String, TextResourceColumn> getFieldColumnAnnotationMap(Class<?> clazz) {
        Map<String, TextResourceColumn> fieldColumnAnnotationMap = beanClassFieldColumnAnnotationMapCache.getOrDefault(clazz, null);
        if (fieldColumnAnnotationMap != null) {
            return fieldColumnAnnotationMap;
        }

        fieldColumnAnnotationMap = new HashMap<>();
        String[] classFieldNames = ClassUtils.getBeanClassFields(clazz);
        for (var fieldName : classFieldNames) {
            var field = ClassUtils.getField(clazz, fieldName, true, true);
            if (field == null) {
                continue;
            }
            var columnAnnotation = field.getAnnotation(TextResourceColumn.class);
            if (columnAnnotation != null) {
                fieldColumnAnnotationMap.put(fieldName, columnAnnotation);
            }
        }

        beanClassFieldColumnAnnotationMapCache.put(clazz, fieldColumnAnnotationMap);

        return fieldColumnAnnotationMap;
    }

    public static Map<String, String> getHeaderFieldMap(Class<?> clazz) {
        var fieldColumnAnnotationMap = getFieldColumnAnnotationMap(clazz);
        Map<String, String> headerFieldMap = new HashMap<>();
        fieldColumnAnnotationMap.forEach((fieldName, columnAnnotation) -> {
            for (var headerName : columnAnnotation.headers()) {
                headerFieldMap.put(headerName, fieldName);
            }
        });
        return headerFieldMap;
    }

    public static Map<String, String> getFieldHeaderMap(Class<?> clazz) {
        var fieldColumnAnnotationMap = getFieldColumnAnnotationMap(clazz);
        Map<String, String> fieldHeaderMap = new HashMap<>();
        fieldColumnAnnotationMap.forEach((fieldName, columnAnnotation) -> {
            var headers = columnAnnotation.headers();
            if (headers.length > 0) {
                fieldHeaderMap.put(fieldName, headers[0]);
            }
        });
        return fieldHeaderMap;
    }

    public static Map<String, Class<? extends TextResourceConverter>> getFieldConverterMap(Class<?> clazz) {
        var fieldColumnAnnotationMap = getFieldColumnAnnotationMap(clazz);
        Map<String, Class<? extends TextResourceConverter>> fieldConverterMap = new HashMap<>();
        fieldColumnAnnotationMap.forEach((fieldName, columnAnnotation) -> {
            Class<? extends TextResourceConverter> converterClass = columnAnnotation.converter();
            fieldConverterMap.put(fieldName, converterClass);
        });
        return fieldConverterMap;
    }

    public static List<String> getLinkedFields(Class<?> clazz) {
        var fieldColumnAnnotationMap = getFieldColumnAnnotationMap(clazz);

        List<Object[]> seqFieldNames = new ArrayList<>();
        for (var fieldName : ClassUtils.getBeanClassFields(clazz)) {
            var columnAnnotation = fieldColumnAnnotationMap.getOrDefault(fieldName, null);
            if (columnAnnotation == null) {
                continue;
            }

            var seq = columnAnnotation.seq();
            seqFieldNames.add(new Object[]{seq, fieldName});
        }
        return seqFieldNames.stream()
                .sorted(Comparator.comparing(seqFieldName -> (int) seqFieldName[0]))
                .map(seqFieldName -> (String) seqFieldName[1])
                .collect(Collectors.toList());
    }

    public static List<String> getHeaderLine(Class<?> clazz) {
        List<String> headerLine = new ArrayList<>();
        var fieldColumnAnnotationMap = getFieldColumnAnnotationMap(clazz);
        var linkedFieldNames = getLinkedFields(clazz);
        linkedFieldNames.forEach(fieldName -> {
            var columnAnnotation = fieldColumnAnnotationMap.getOrDefault(fieldName, null);
            if (columnAnnotation == null) {
                return;
            }

            var headers = columnAnnotation.headers();
            if (headers.length > 0) {
                headerLine.add(headers[0]);
            } else {
                headerLine.add(fieldName);
            }
        });
        return headerLine;
    }

    public static List<LinkedHashMap<String, String>> lineToMaps(List<List<String>> lines,
                                                                 Map<String, String> headerFieldMap) {
        if (lines == null || lines.size() <= 1) {
            return new ArrayList<>();
        }

        List<LinkedHashMap<String, String>> maps = new ArrayList<>();
        List<String> headerLine = lines.get(0);
        var headerCount = headerLine.size();
        for (var i = 1; i < lines.size(); i++) {
            List<String> dataLine = lines.get(i);
            LinkedHashMap<String, String> dataMap = new LinkedHashMap<>();
            for (var j = 0; j < dataLine.size(); j++) {
                var value = dataLine.get(j);
                if (j >= headerCount) {
                    continue;
                }
                var headerName = headerLine.get(j);
                String fieldName;
                if (headerFieldMap == null) {
                    fieldName = headerName;
                } else {
                    fieldName = headerFieldMap.getOrDefault(headerName, headerName);
                }
                dataMap.put(fieldName, value);
            }
            maps.add(dataMap);
        }

        return maps;
    }

    public static List<List<String>> mapToLines(List<LinkedHashMap<String, String>> maps,
                                                Map<String, String> fieldHeaderMap) {
        if (maps == null) {
            return new ArrayList<>();
        }

        List<List<String>> lines = new ArrayList<>();
        List<String> headerLine = new ArrayList<>();
        for (var i = 0; i < maps.size(); i++) {
            var map = maps.get(i);
            List<String> dataLine = new ArrayList<>();
            final var currentIndex = i;
            map.forEach((fieldName, fieldValue) -> {
                if (currentIndex == 0) {
                    String headerName;
                    if (fieldHeaderMap == null) {
                        headerName = fieldName;
                    } else {
                        headerName = fieldHeaderMap.getOrDefault(fieldName, fieldName);
                    }
                    headerLine.add(headerName);
                }
                dataLine.add(fieldValue);
            });

            if (i == 0) {
                lines.add(headerLine);
            }
            lines.add(dataLine);
        }
        return lines;
    }

    public static <T> List<T> mapToObjs(List<LinkedHashMap<String, String>> maps, Class<T> clazz) {
        if (maps == null || maps.isEmpty()) {
            return new ArrayList<>();
        }

        var fieldConverterMap = getFieldConverterMap(clazz);
        List<T> objs = new ArrayList<>();
        maps.forEach(dataMap -> {
            var obj = ClassUtils.createInstance(clazz);
            dataMap.forEach((fieldName, fieldValue) -> {
                var converterClass = fieldConverterMap.getOrDefault(fieldName, null);
                Object convertValue = converterClass == null ? fieldValue : BeanCommon.getBean(converterClass).convert(fieldValue);
                ClassUtils.setObjFieldValue(obj, fieldName, convertValue);
            });
            objs.add(obj);
        });
        return objs;
    }

    public static <T> List<LinkedHashMap<String, String>> objToMaps(List<T> objs) {
        if (objs == null || objs.isEmpty()) {
            return new ArrayList<>();
        }

        var clazz = objs.get(0).getClass();
        var fieldConverterMap = getFieldConverterMap(clazz);
        var linkedFieldNames = getLinkedFields(clazz);
        List<LinkedHashMap<String, String>> maps = new ArrayList<>();
        objs.forEach(obj -> {
            LinkedHashMap<String, String> dataMap = new LinkedHashMap<>();
            linkedFieldNames.forEach(fieldName -> {
                var fieldValue = ClassUtils.getObjFieldValue(obj, fieldName);
                var converterClass = fieldConverterMap.getOrDefault(fieldName, null);
                String convertValue = converterClass == null ? (String) fieldValue : BeanCommon.getBean(converterClass).reconvert(fieldValue);
                dataMap.put(fieldName, convertValue);
            });
            maps.add(dataMap);
        });
        return maps;
    }

}
