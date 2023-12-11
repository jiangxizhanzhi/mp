package cn.cyberpeace.mp.log.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Builder;
import lombok.Data;

@Data
@HeadRowHeight(20)
@Builder
public class ExportLogEnBo {

    @ExcelProperty("Username")
    @ColumnWidth(15)
    private String username;

    @ExcelProperty("Modules")
    @ColumnWidth(12)
    private String name;

    @ExcelProperty("Operation type")
    @ColumnWidth(12)
    private String operate_type;

    @ExcelProperty(value = "Operand")
    @ColumnWidth(40)
    private String operate_object;

    @ExcelProperty("Operation time")
    @ColumnWidth(20)
    private String operate_time;

    @ExcelProperty("Content")
    @ColumnWidth(40)
    private String content;

}