package cn.cyberpeace.mp.cscene.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author zhangjiaxin
 * @date 2022年11月5日下午2:17:59
 */
@Slf4j
public class ResponseUtil {

    /**
     * 把输入流以文件的方式传输到前端
     * 
     * @param fileName
     * @param inputStream
     * @param response
     */
    public static void transferStream(String fileName, InputStream inputStream, HttpServletResponse response) {
        // 以流的形式下载文件。
        OutputStream outputStream = null;
        try {
            // 清空response
            response.reset();

            // 设置返回
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            outputStream = response.getOutputStream();
            StreamUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error("setResponse error:", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error("setResponse error:", e);
            }
        }
    }

}
