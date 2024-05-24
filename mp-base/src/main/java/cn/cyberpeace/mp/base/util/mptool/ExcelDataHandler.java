package cn.cyberpeace.mp.base.util.mptool;

import cn.cyberpeace.mp.base.constant.ExcelHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hssf.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * excel数据处理器 需要什么方法自己加
 */
@Slf4j
public class ExcelDataHandler {

	/**
	 * excel 文件导出
	 * @param list 导出数据
	 * @param sheetName sheet名
	 * @param passFields 过滤字段
	 * @param excelHeader 导出列头
	 * @param <T> 数据bean
	 * @return 文件流
	 */
	public static <T> File exportExcel(List<T> list, String filePath, String sheetName,
									   List<String> passFields, ExcelHeader excelHeader) {

		File file = new File(filePath);
		// 校验文件是否存在 不存在则新建
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			boolean isCreated = parentFile.mkdirs();
			if (!isCreated) {
				log.error("exportExcel fail! filePath=[{}]",filePath);
			}
		}

		if (!file.exists()) {
			try	(HSSFWorkbook hssfWorkbook = new HSSFWorkbook()) {
				hssfWorkbook.write(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try (HSSFWorkbook wb = new HSSFWorkbook(Files.newInputStream(Paths.get(filePath)))){
			// 创建HSSFSheet对象
			HSSFSheet sheet = wb.createSheet(sheetName);
			// 创建HSSFRow对象
			HSSFRow row = sheet.createRow(0);
			// 在excel表中添加表头
			for (int i = 0; i < excelHeader.getCnName().length; i++) {
				// 创建HSSFCell对象
				HSSFCell cell = row.createCell(i);
				HSSFRichTextString text = new HSSFRichTextString(excelHeader.getCnName()[i]);
				cell.setCellValue(text);
			}
			int rowNum = 1;
			if (list.size() != 0) {
				T t = list.get(0);
				Field[] fields = t.getClass().getDeclaredFields();
				Field[] superClassFields = t.getClass().getSuperclass().getDeclaredFields();
				Field[] allFields = ArrayUtils.addAll(fields, superClassFields);
				for (T customersForm : list) {
					int cellNum = 0;
					HSSFRow dataRow = sheet.createRow(rowNum);
					for (Field field : allFields) {
						// 设置允许通过反射访问私有变量
						field.setAccessible(true);
						// 过滤无用字段值
						if (passFields.contains(field.getName())) {
							continue;
						}
						// 获取字段的值
						String value = "";
						Object obj = field.get(customersForm);
						if (obj != null) {
							value = obj.toString();
						}
						// 获取字段属性名称 设置单元格内容
						dataRow.createCell(cellNum).setCellValue(value);
						cellNum++;
					}
					rowNum++;
				}
			}
			// 输出Excel文件
			wb.write(file);

		} catch (Exception e) {
			log.error(
					"Construct XSSFSheet object failed! file_path=[{}], sheet_name=[{}], catch error:",
					filePath, sheetName, e);
		}

		return file;
	}

}

