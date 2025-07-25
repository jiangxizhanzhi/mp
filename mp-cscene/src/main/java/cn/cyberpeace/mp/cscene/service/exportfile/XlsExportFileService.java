package cn.cyberpeace.mp.cscene.service.exportfile;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class XlsExportFileService implements ExportFileService {
    @Override
    public <D, R extends QueryParams> void exportFile(HttpServletResponse response, PageIterator<D, R> pageIterator, String filename) {
        filename = filename + ".xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));

        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).excelType(ExcelTypeEnum.XLS).build()) {
            WriteSheet sheet = EasyExcel.writerSheet()
                    .head(pageIterator.getDataClass())
                    .build();
            while (pageIterator.hasNext()) {
                var batch = pageIterator.next();
                excelWriter.write(batch, sheet);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("写入想要信息失败"));
        }
    }
}
