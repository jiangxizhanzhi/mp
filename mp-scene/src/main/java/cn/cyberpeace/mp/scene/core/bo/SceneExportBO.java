package cn.cyberpeace.mp.scene.core.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * @Description: 场景统计导出对象
 * @Author qiaokang
 * @Date：2023/2/15 16:57
 */
@Data
@HeadRowHeight(20)
public class SceneExportBO {

    @ExcelProperty("日期")
    @ColumnWidth(20)
    private String date;

    @ExcelProperty("成功场景数")
    @ColumnWidth(25)
    private Long successCount;

    @ExcelProperty("失败场景数")
    @ColumnWidth(25)
    private Long failCount;

    @ExcelProperty(value = "失败率")
    @ColumnWidth(20)
    private String failRate;

}
