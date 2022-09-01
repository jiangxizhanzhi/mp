package cn.cyberpeace.mp.base.common.textresource.exel;

import cn.cyberpeace.mp.base.common.textresource.base.TextResourceBase;
import com.alibaba.excel.EasyExcel;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelCommon {

    private ExcelCommon() {}

    public static List<LinkedHashMap<String, String>> read(InputStream is, Map<String, String> headerFieldMap) {
        List<Map<Integer, String>> sourceLines = EasyExcel.read(is).headRowNumber(0).sheet().doReadSync();
        var lines = sourceLines.stream().map(sourceLine -> {
            List<Object[]> orderingLine = new ArrayList<>();
            sourceLine.forEach((seq, value) -> orderingLine.add(new Object[]{seq, value}));
            return orderingLine.stream()
                    .sorted(Comparator.comparing(item -> (Integer) item[0]))
                    .map(item -> item[1] == null ? null : (String) item[1])
                    .collect(Collectors.toList());
        }).collect(Collectors.toList());
        return TextResourceBase.lineToMaps(lines, headerFieldMap);
    }

    public static <T> List<T> read(InputStream is, Class<T> clazz) {
        Map<String, String> headerFieldMap = TextResourceBase.getHeaderFieldMap(clazz);
        var maps = read(is, headerFieldMap);
        return TextResourceBase.mapToObjs(maps, clazz);
    }

    public static void write(List<LinkedHashMap<String, String>> maps, OutputStream os,
                             Map<String, String> headerFieldMap) {
        if (maps == null || maps.isEmpty()) {
            return;
        }

        var lines = TextResourceBase.mapToLines(maps, headerFieldMap);
        EasyExcel.write(os).sheet().doWrite(lines);
    }

    public static <T> void write(List<T> objs, OutputStream os) {
        if (objs == null || objs.isEmpty()) {
            return;
        }

        var clazz = objs.get(0).getClass();
        Map<String, String> fieldHeaderMap = TextResourceBase.getFieldHeaderMap(clazz);
        var maps = TextResourceBase.objToMaps(objs);
        write(maps, os, fieldHeaderMap);
    }

    public static void writeHeaderOnly(List<String> headerLine, OutputStream os) {
        EasyExcel.write(os).sheet().doWrite(List.of(headerLine));
    }

    public static <T> void writeHeaderOnly(Class<T> clazz, OutputStream os) {
        writeHeaderOnly(TextResourceBase.getHeaderLine(clazz), os);
    }

}
