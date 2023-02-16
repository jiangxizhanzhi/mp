package cn.cyberpeace.mp.scene.core.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Builder;
import lombok.Data;

/**
 * @Description: 场景统计导出EN对象
 * @Author qiaokang
 * @Date：2023/2/15 16:57
 */
@Data
@HeadRowHeight(20)
@Builder
public class SceneExportEnBO {

    @ExcelProperty("Date")
    @ColumnWidth(20)
    private String date;

    @ExcelProperty("Success Scene Count")
    @ColumnWidth(25)
    private Long successCount;

    @ExcelProperty("Fail Scene Count")
    @ColumnWidth(25)
    private Long failCount;

    @ExcelProperty(value = "Fail Rate")
    @ColumnWidth(20)
    private String failRate;
    
}
