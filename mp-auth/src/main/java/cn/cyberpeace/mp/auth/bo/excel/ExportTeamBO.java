package cn.cyberpeace.mp.auth.bo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportTeamBO {
    @ExcelProperty("战队名称")
    @ColumnWidth(15)
    private String name;
    @ExcelProperty("战队描述(非必填)")
    @ColumnWidth(30)
    private String description;
    @ExcelProperty("战队标签(非必填)")
    @ColumnWidth(30)
    private String tag;
}
