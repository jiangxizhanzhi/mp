package cn.cyberpeace.mp.auth.bo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Builder;
import lombok.Data;

@Data
@HeadRowHeight(20)
@Builder
public class ExportUserBo {

    @ExcelProperty("用户名")
    @ColumnWidth(15)
    private String username;

    @ExcelProperty("姓名")
    @ColumnWidth(12)
    private String name;

    @ExcelProperty("组织名称")
    @ColumnWidth(80)
    private String org;

    @ExcelProperty(value = "状态")
    @ColumnWidth(12)
    private String stat;

    @ExcelProperty("角色")
    @ColumnWidth(80)
    private String roles;

}
