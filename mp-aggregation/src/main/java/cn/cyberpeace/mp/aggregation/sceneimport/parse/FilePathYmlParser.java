package cn.cyberpeace.mp.aggregation.sceneimport.parse;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.lucene.util.IOUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName FilePathYmlParser
 * @Description
 * @Author maojun
 * @Date 2021/11/2
 * @Version 1.0.0
 * @see YmlParse
 */
@Slf4j
class FilePathYmlParser<T> implements YmlParse<T> {
    private String filePath;

    private Class<T> resultType;

    /**
     * FilePathYmlParser
     *
     * @param filePath
     * @param resultType
     */
    public FilePathYmlParser(String filePath, Class<T> resultType) {
        this.filePath = filePath;
        this.resultType = resultType;
    }

    /**
     * parse
     *
     * @return
     */
    @Override
    public T parse() {
        File file = new File(filePath);

        try (InputStream inputStream = new FileInputStream(file)){
            BaseConstructor constructor = new Constructor();
            constructor.setPropertyUtils(new YamlPropertyParseUtils());
            Yaml yaml = new Yaml(constructor);
            return yaml.loadAs(inputStream, resultType);
        } catch (FileNotFoundException e) {
            log.error("yml file not exists , file path is {}, {}", filePath, ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }  catch (IOException e) {
            log.error("Failed to close the input stream for yml file, {}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }catch (Exception e) {
            log.error("failed to parse yml , file path is {}, {}", filePath, ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
    }
}
