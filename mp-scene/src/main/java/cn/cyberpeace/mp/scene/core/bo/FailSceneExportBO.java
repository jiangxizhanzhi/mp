package cn.cyberpeace.mp.scene.core.bo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * @Description: 失败场景详情导出对象
 * @Author qiaokang
 * @Date：2023/2/15 16:57
 */
@Data
@HeadRowHeight(20)
public class FailSceneExportBO {

    @ExcelProperty("日期")
    @ColumnWidth(25)
    private String date;

    @ExcelProperty("失败场景名")
    @ColumnWidth(40)
    private String name;

    @ExcelIgnore
    private String referenceType;

    @ExcelProperty("场景归属")
    @ColumnWidth(30)
    private String referenceName;

    @ExcelProperty(value = "失败信息")
    @ColumnWidth(40)
    private String processMessage;
    
}
