package cn.cyberpeace.mp.log.dto;

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
public class ExportOperateLogBo {

    @ExcelProperty("用户名")
    @ColumnWidth(15)
    private String username;

    @ExcelProperty("姓名")
    @ColumnWidth(15)
    private String name;

    @ExcelProperty("登录时间")
    @ColumnWidth(30)
    private String time;

    @ExcelProperty(value = "IP")
    @ColumnWidth(20)
    private String ip;
}
