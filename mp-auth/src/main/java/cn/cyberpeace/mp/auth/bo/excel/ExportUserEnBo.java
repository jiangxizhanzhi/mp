package cn.cyberpeace.mp.auth.bo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Builder;
import lombok.Data;


@Data
@HeadRowHeight(20)
@Builder
public class ExportUserEnBo {

    @ExcelProperty("Username")
    @ColumnWidth(15)
    private String username;

    @ExcelProperty("Name")
    @ColumnWidth(12)
    private String name;

    @ExcelProperty("Organization")
    @ColumnWidth(80)
    private String org;

    @ExcelProperty(value = "Status")
    @ColumnWidth(12)
    private String stat;

    @ExcelProperty("Role")
    @ColumnWidth(80)
    private String roles;

}
