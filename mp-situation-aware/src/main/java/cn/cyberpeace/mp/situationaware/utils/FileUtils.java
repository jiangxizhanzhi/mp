package cn.cyberpeace.mp.situationaware.utils;

import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    /**
     * 逐行读取资源文件
     * @param resources
     * @return
     * @throws IOException
     */
    public static String readLine(Resource[] resources) throws IOException {
        String json = "";
        for (Resource resource : resources){
            InputStream inputStream = resource.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String data = null;
            while((data = br.readLine()) != null) {
                json += data;
            }

            br.close();
            isr.close();
            inputStream.close();
        }
        return json;
    }
}
