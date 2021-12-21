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
public class ExportUser {

    @ExcelProperty("用户名")
    @ColumnWidth(15)
    private String username;

    @ExcelProperty("姓名")
    @ColumnWidth(12)
    private String name;

    @ExcelProperty("组织名称")
    @ColumnWidth(80)
    private String org;

    @ExcelProperty(value = "状态", converter = Stat2StringConverter.class)
    @ColumnWidth(12)
    private String stat;

    @ExcelProperty("角色")
    @ColumnWidth(80)
    private String roles;

}
