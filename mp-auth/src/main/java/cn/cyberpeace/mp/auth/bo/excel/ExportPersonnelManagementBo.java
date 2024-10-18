package cn.cyberpeace.mp.auth.bo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportPersonnelManagementBo {
    @ExcelProperty("战队名称")
    @ColumnWidth(15)
    private String teamName;
    @ExcelProperty("用户名")
    @ColumnWidth(15)
    private String username;
}
