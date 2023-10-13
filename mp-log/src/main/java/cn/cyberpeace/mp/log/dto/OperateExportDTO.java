package cn.cyberpeace.mp.log.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperateExportDTO {
    @ExcelProperty("用户名")
    private String name;
    @ExcelProperty("模块")
    private String module;
    @ExcelProperty("操作类型")
    private String operateType;
    @ExcelProperty("操作对象")
    private String operateTarget;
    @ExcelProperty("操作时间")
    private String operateTime;
    @ExcelProperty("内容")
    private String content;
}
