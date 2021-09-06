package cn.cyberpeace.mp.auth.dto.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public class Stat2StringConverter implements Converter<String> {
    @Override
    public Class supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
    }

    @Override
    public CellData convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
//        if (Objects.equals(value, UserModel.Status.ENABLED.toString())) {
//            return new CellData("正常");
//        }
//
//        if (Objects.equals(value, UserModel.Status.DISABLE.toString())) {
//            return new CellData("禁用");
//        }
//
//        if (Objects.equals(value, UserModel.Status.PENDING.toString())) {
//            return new CellData("待审核");
//        }
//
//        if (Objects.equals(value, UserModel.Status.EXPIRED.toString())) {
//            return new CellData("过期");
//        }
        return new CellData(value);
    }
}
