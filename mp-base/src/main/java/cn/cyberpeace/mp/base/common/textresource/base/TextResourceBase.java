package cn.cyberpeace.mp.base.common.textresource.base;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.textresource.converter.TextResourceConverter;
import cn.cyberpeace.mp.base.common.textresource.stereotype.TextResourceColumn;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;

import java.lang.reflect.Field;
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
        for (String fieldName : classFieldNames) {
            Field field = ClassUtils.getField(clazz, fieldName, true, true);
            if (field == null) {
                continue;
            }
            TextResourceColumn columnAnnotation = field.getAnnotation(TextResourceColumn.class);
            if (columnAnnotation != null) {
                fieldColumnAnnotationMap.put(fieldName, columnAnnotation);
            }
        }

        beanClassFieldColumnAnnotationMapCache.put(clazz, fieldColumnAnnotationMap);

        return fieldColumnAnnotationMap;
    }

    public static Map<String, String> getHeaderFieldMap(Class<?> clazz) {
        Map<String, TextResourceColumn> fieldColumnAnnotationMap = getFieldColumnAnnotationMap(clazz);
        Map<String, String> headerFieldMap = new HashMap<>();
        fieldColumnAnnotationMap.forEach((fieldName, columnAnnotation) -> {
            for (String headerName : columnAnnotation.headers()) {
                headerFieldMap.put(headerName, fieldName);
            }
        });
        return headerFieldMap;
    }

    public static Map<String, String> getFieldHeaderMap(Class<?> clazz) {
        Map<String, TextResourceColumn> fieldColumnAnnotationMap = getFieldColumnAnnotationMap(clazz);
        Map<String, String> fieldHeaderMap = new HashMap<>();
        fieldColumnAnnotationMap.forEach((fieldName, columnAnnotation) -> {
            String[] headers = columnAnnotation.headers();
            if (headers.length > 0) {
                fieldHeaderMap.put(fieldName, headers[0]);
            }
        });
        return fieldHeaderMap;
    }

    public static Map<String, Class<? extends TextResourceConverter>> getFieldConverterMap(Class<?> clazz) {
        Map<String, TextResourceColumn> fieldColumnAnnotationMap = getFieldColumnAnnotationMap(clazz);
        Map<String, Class<? extends TextResourceConverter>> fieldConverterMap = new HashMap<>();
        fieldColumnAnnotationMap.forEach((fieldName, columnAnnotation) -> {
            Class<? extends TextResourceConverter> converterClass = columnAnnotation.converter();
            fieldConverterMap.put(fieldName, converterClass);
        });
        return fieldConverterMap;
    }

    public static List<String> getLinkedFields(Class<?> clazz) {
        Map<String, TextResourceColumn> fieldColumnAnnotationMap = getFieldColumnAnnotationMap(clazz);

        List<Object[]> seqFieldNames = new ArrayList<>();
        for (String fieldName : ClassUtils.getBeanClassFields(clazz)) {
            TextResourceColumn columnAnnotation = fieldColumnAnnotationMap.getOrDefault(fieldName, null);
            if (columnAnnotation == null) {
                continue;
            }

            int seq = columnAnnotation.seq();
            seqFieldNames.add(new Object[]{seq, fieldName});
        }
        return seqFieldNames.stream()
                .sorted(Comparator.comparing(seqFieldName -> (int) seqFieldName[0]))
                .map(seqFieldName -> (String) seqFieldName[1])
                .collect(Collectors.toList());
    }

    public static List<String> getHeaderLine(Class<?> clazz) {
        List<String> headerLine = new ArrayList<>();
        Map<String, TextResourceColumn> fieldColumnAnnotationMap = getFieldColumnAnnotationMap(clazz);
        List<String> linkedFieldNames = getLinkedFields(clazz);
        linkedFieldNames.forEach(fieldName -> {
            TextResourceColumn columnAnnotation = fieldColumnAnnotationMap.getOrDefault(fieldName, null);
            if (columnAnnotation == null) {
                return;
            }

            String[] headers = columnAnnotation.headers();
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
        int headerCount = headerLine.size();
        for (int i = 1; i < lines.size(); i++) {
            List<String> dataLine = lines.get(i);
            LinkedHashMap<String, String> dataMap = new LinkedHashMap<>();
            for (int j = 0; j < dataLine.size(); j++) {
                String value = dataLine.get(j);
                if (j >= headerCount) {
                    continue;
                }
                String headerName = headerLine.get(j);
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
        for (int i = 0; i < maps.size(); i++) {
            LinkedHashMap<String, String> map = maps.get(i);
            List<String> dataLine = new ArrayList<>();
            final int currentIndex = i;
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

        Map<String, Class<? extends TextResourceConverter>> fieldConverterMap = getFieldConverterMap(clazz);
        List<T> objs = new ArrayList<>();
        maps.forEach(dataMap -> {
            T obj = ClassUtils.createInstance(clazz);
            dataMap.forEach((fieldName, fieldValue) -> {
                Class<? extends TextResourceConverter> converterClass = fieldConverterMap.getOrDefault(fieldName, null);
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

        Class<?> clazz = objs.get(0).getClass();
        Map<String, Class<? extends TextResourceConverter>> fieldConverterMap = getFieldConverterMap(clazz);
        List<String> linkedFieldNames = getLinkedFields(clazz);
        List<LinkedHashMap<String, String>> maps = new ArrayList<>();
        objs.forEach(obj -> {
            LinkedHashMap<String, String> dataMap = new LinkedHashMap<>();
            linkedFieldNames.forEach(fieldName -> {
                Object fieldValue = ClassUtils.getObjFieldValue(obj, fieldName);
                Class<? extends TextResourceConverter> converterClass = fieldConverterMap.getOrDefault(fieldName, null);
                String convertValue = converterClass == null ? (String) fieldValue : BeanCommon.getBean(converterClass).reconvert(fieldValue);
                dataMap.put(fieldName, convertValue);
            });
            maps.add(dataMap);
        });
        return maps;
    }

}
