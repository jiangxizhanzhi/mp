package cn.cyberpeace.mp.log.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@HeadRowHeight(20)
@Builder
public class ExportLogBo {

    @ExcelProperty("用户名")
    @ColumnWidth(15)
    private String username;

    @ExcelProperty("模块")
    @ColumnWidth(12)
    private String module;

    @ExcelProperty("操作类型")
    @ColumnWidth(12)
    private String operate_type;

    @ExcelProperty(value = "操作对象")
    @ColumnWidth(40)
    private String operate_object;

    @ExcelProperty("操作时间")
    @ColumnWidth(20)
    private LocalDateTime operate_time;

    @ExcelProperty("详细内容")
    @ColumnWidth(40)
    private String content;

}

