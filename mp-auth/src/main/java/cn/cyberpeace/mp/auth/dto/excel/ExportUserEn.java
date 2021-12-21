package cn.cyberpeace.mp.auth.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@HeadRowHeight(20)
@Builder
public class ExportUserEn {

    @ExcelProperty("Username")
    @ColumnWidth(15)
    private String username;

    @ExcelProperty("Name")
    @ColumnWidth(12)
    private String name;

    @ExcelProperty("Organization")
    @ColumnWidth(80)
    private String org;

    @ExcelProperty(value = "Status", converter = Stat2StringConverter.class)
    @ColumnWidth(12)
    private String stat;

    @ExcelProperty("Role")
    @ColumnWidth(80)
    private String roles;

}
