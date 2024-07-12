 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ExportUserEnBo.java.
 // File size 884.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.bo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ExportUserEnBo
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Data
@HeadRowHeight(20)
@Builder
public class ExportUserEnBo {

    @ExcelProperty("Username")
    @ColumnWidth(15)
    private String username;

    @ExcelProperty("Name")
    @ColumnWidth(12)
    private String name;

    @ExcelProperty("Organization")
    @ColumnWidth(80)
    private String org;

    @ExcelProperty(value = "Status")
    @ColumnWidth(12)
    private String stat;

    @ExcelProperty("Role")
    @ColumnWidth(80)
    private String roles;

}
