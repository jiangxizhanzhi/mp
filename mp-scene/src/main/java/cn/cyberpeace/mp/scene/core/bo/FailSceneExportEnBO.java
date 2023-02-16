package cn.cyberpeace.mp.scene.core.bo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Builder;
import lombok.Data;

/**
 * @Description: 失败场景详情导出EN对象
 * @Author qiaokang
 * @Date：2023/2/15 16:57
 */
@Data
@HeadRowHeight(20)
@Builder
public class FailSceneExportEnBO {

    @ExcelProperty("Date")
    @ColumnWidth(25)
    private String date;

    @ExcelProperty("Failed Scene Name")
    @ColumnWidth(40)
    private String name;

    @ExcelIgnore
    private String referenceType;

    @ExcelProperty("Scene Reference")
    @ColumnWidth(30)
    private String referenceName;

    @ExcelProperty(value = "Fail Info")
    @ColumnWidth(40)
    private String processMessage;
    
}
