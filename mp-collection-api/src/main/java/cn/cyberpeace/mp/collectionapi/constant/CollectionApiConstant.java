package cn.cyberpeace.mp.collectionapi.constant;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;

public class CollectionApiConstant {
    private CollectionApiConstant() {

    }

    public static Object lock = new Object();

    public static BigDecimal multiple = null;

    public static long multipleNum(long num) {
        init();
        return multiple.multiply(BigDecimal.valueOf(num)).longValue();
    }

    private static void init() {
        if (multiple == null) {
            synchronized (lock) {
                if (multiple == null) {
                    var baseAppConfig = BeanCommon.getBean(BaseAppConfig.class);
                    String logDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, baseAppConfig.getHomeDir(), "multiple");
                    File valueFile = new File(logDir + FileUtil.FILE_SEPARATOR + "value");
                    if (!valueFile.exists()) {
                        multiple = BigDecimal.valueOf(1);
                    } else {
                        var list = FileUtil.readLines(valueFile, "utf8");
                        if (list != null && list.size() > 0) {
                            multiple = BigDecimal.valueOf(Double.valueOf(list.get(0)));
                        } else {
                            multiple = BigDecimal.valueOf(1);
                        }
                    }
                }
            }
        }
    }
}
