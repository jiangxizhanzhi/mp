package cn.cyberpeace.mp.devopscluster.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    // 适用于xlsx表格读取列表
    public static <T> List<T> readExcelList(InputStream inputStream, Class<T> clazz) throws Exception {
        List<T> resultList = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // 获取对象的所有字段（包括私有字段）
        Field[] fields = clazz.getDeclaredFields();

        // 迭代每一行（从第 2 行开始，跳过表头）
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            // 创建泛型对象实例
            T obj = clazz.getDeclaredConstructor().newInstance();

            // 为每一列的值设置到对应的字段
            for (int j = 0; j < fields.length - 1; j++) {
                Field field = fields[j];
                field.setAccessible(true);  // 允许访问私有字段
                String cellValue = getCellValue(row.getCell(j));
                field.set(obj, convertValue(cellValue, field.getType()));
            }
            // 判断obj的每个属性是否都为空
            if(!isObjectEmpty(obj)){
                resultList.add(obj);
            }
        }

        workbook.close();
        return resultList;
    }

    // 帮助方法：处理单元格的值
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    // 帮助方法：根据字段类型转换单元格值
    private static Object convertValue(String value, Class<?> fieldType) {
        if (fieldType == String.class) {
            return value;
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return Integer.parseInt(value);
        } else if (fieldType == Double.class || fieldType == double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }

    public static <T> boolean isObjectEmpty(T obj) throws IllegalAccessException {
        // 获取对象的所有字段
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);  // 允许访问私有字段
            Object value = field.get(obj);  // 获取字段的值

            // 检查字段的值是否为 null 或空字符串
            if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
                // 如果有一个字段不为 null 或不为空，则对象不为空
                return false;
            }
        }

        // 如果所有字段都为 null 或空，则认为对象是空的
        return true;
    }

}
