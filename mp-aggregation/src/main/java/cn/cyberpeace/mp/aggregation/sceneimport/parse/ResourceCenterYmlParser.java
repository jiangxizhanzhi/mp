package cn.cyberpeace.mp.aggregation.sceneimport.parse;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.apache.lucene.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description 解析资管的yml
 * @Author maojun
 * @Date 2021/10/12
 */
public final class ResourceCenterYmlParser {
    private static Logger log = LoggerFactory.getLogger(ResourceCenterYmlParser.class);

    public static YmlParseResult parse(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            BaseConstructor constructor = new Constructor();
            constructor.setPropertyUtils(new YamlPropertyParseUtils());
            Yaml yaml = new Yaml(constructor);
            return yaml.loadAs(inputStream, YmlParseResult.class);
        } catch (FileNotFoundException e) {
            log.error("yml file not exists , file path is {}", filePath, e);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        } catch (Exception e) {
            log.error("failed to parse yml , file path is {}", filePath, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        } finally {
            try {
                if (inputStream != null) {
                    IOUtils.close(inputStream);
                }
            } catch (IOException e) {
                log.error("failed to close the inputStream");
            }
        }
    }
}
