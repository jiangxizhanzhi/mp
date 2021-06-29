package cn.cyberpeace.mp.auth.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

@Data
@HeadRowHeight(20)
public class ExportUser {

    @ExcelProperty("用户名")
    @ColumnWidth(15)
    private String username;

    @ExcelProperty("姓名")
    @ColumnWidth(12)
    private String name;

    @ExcelProperty("组织")
    @ColumnWidth(50)
    private String org;

    @ExcelProperty("状态")
    @ColumnWidth(12)
    private String stat;

    @ExcelProperty("角色")
    @ColumnWidth(50)
    private String roles;

}
