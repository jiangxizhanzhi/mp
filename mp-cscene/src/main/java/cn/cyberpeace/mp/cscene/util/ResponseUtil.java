package cn.cyberpeace.mp.cscene.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author zhangjiaxin
 * @date 2022年11月5日下午2:17:59
 */
@Slf4j
public class ResponseUtil {

    /**
     * 生成文件响应
     * 
     * @param file     返回的文件
     * @param delete   是否删除本地文件
     * @param response 响应
     */
    public static void exportFile(File file, Boolean delete, HttpServletResponse response) {
        // 以流的形式下载文件。
        InputStream fis = null;
        OutputStream outputStream = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);

            // 清空response
            response.reset();

            // 设置返回
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + file.getName());
            outputStream = response.getOutputStream();
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            log.error("setResponse error:", e);
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error("setResponse error:", e);
            }

            if (delete && null != file && file.exists()) {
                file.delete();
            }
        }
    }

}
