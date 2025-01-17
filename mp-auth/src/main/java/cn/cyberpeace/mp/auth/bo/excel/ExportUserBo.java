// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ExportUserBo.java.
 // File size 839.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Builder;
import lombok.Data;

/**
 * 导出用户bo
 *
 * @author 二竹
 * @date 2025/01/17
 */
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
