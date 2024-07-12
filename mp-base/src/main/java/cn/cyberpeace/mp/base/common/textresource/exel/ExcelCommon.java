 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ExcelCommon.java.
 // File size 3695.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.common.textresource.exel;

import cn.cyberpeace.mp.base.common.textresource.base.TextResourceBase;
import com.alibaba.excel.EasyExcel;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @ClassName ExcelCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class ExcelCommon {

    /**
     * ExcelCommon
     */
    private ExcelCommon() {}

    /**
     * read
     *
     * @param is
     * @param headerFieldMap
     * @return
     */
    public static List<LinkedHashMap<String, String>> read(InputStream is, Map<String, String> headerFieldMap) {
        List<Map<Integer, String>> sourceLines = EasyExcel.read(is).headRowNumber(0).sheet().doReadSync();
        List<List<String>> lines = new ArrayList<>();
        for (Map<Integer, String> integerStringMap : sourceLines) {
            List<Object[]> orderingLine = new ArrayList<>();
            for (Map.Entry<Integer, String> entry : integerStringMap.entrySet()) {
                Integer seq = entry.getKey();
                String value = entry.getValue();
                orderingLine.add(new Object[]{seq, value});
            }
            List<Object[]> toSort = new ArrayList<>();
            for (Object[] item : orderingLine) {
                toSort.add(item);
            }
            toSort.sort(Comparator.comparing(item -> (Integer) item[0]));
            List<String> apply = new ArrayList<>();
            for (Object[] item : toSort) {
                String s = item[1] == null ? null : (String) item[1];
                apply.add(s);
            }
            lines.add(apply);
        }
        return TextResourceBase.lineToMaps(lines, headerFieldMap);
    }

    /**
     * read
     *
     * @param is
     * @param clazz
     * @return
     */
    public static <T> List<T> read(InputStream is, Class<T> clazz) {
        Map<String, String> headerFieldMap = TextResourceBase.getHeaderFieldMap(clazz);
        List<LinkedHashMap<String, String>> maps = read(is, headerFieldMap);
        return TextResourceBase.mapToObjs(maps, clazz);
    }

    /**
     * write
     *
     * @param maps
     * @param os
     * @param headerFieldMap
     */
    public static void write(List<LinkedHashMap<String, String>> maps, OutputStream os,
                             Map<String, String> headerFieldMap) {
        if (maps == null || maps.isEmpty()) {
            return;
        }

        List<List<String>> lines = TextResourceBase.mapToLines(maps, headerFieldMap);
        EasyExcel.write(os).sheet().doWrite(lines);
    }

    /**
     * write
     *
     * @param objs
     * @param os
     */
    public static <T> void write(List<T> objs, OutputStream os) {
        if (objs == null || objs.isEmpty()) {
            return;
        }

        Class<?> clazz = objs.get(0).getClass();
        Map<String, String> fieldHeaderMap = TextResourceBase.getFieldHeaderMap(clazz);
        List<LinkedHashMap<String, String>> maps = TextResourceBase.objToMaps(objs);
        write(maps, os, fieldHeaderMap);
    }

    /**
     * writeHeaderOnly
     *
     * @param headerLine
     * @param os
     */
    public static void writeHeaderOnly(List<String> headerLine, OutputStream os) {
        EasyExcel.write(os).sheet().doWrite(List.of(headerLine));
    }

    /**
     * writeHeaderOnly
     *
     * @param clazz
     * @param os
     */
    public static <T> void writeHeaderOnly(Class<T> clazz, OutputStream os) {
        writeHeaderOnly(TextResourceBase.getHeaderLine(clazz), os);
    }

}
