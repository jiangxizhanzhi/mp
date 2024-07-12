 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ExportUserBo.java.
 // File size 885.
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
 * @ClassName ExportUserBo
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
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
