package cn.cyberpeace.mp.sceneplugincollection.util;

import cn.cyberpeace.mp.base.common.TaskCommon;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Data
public class FlowPcapExportUtil {
    private static LoadingCache<String, FlowPcapExportProcess> cache = CacheBuilder.newBuilder().maximumSize(500)
            .expireAfterAccess(150, TimeUnit.MINUTES)
            .build(new CacheLoader<String, FlowPcapExportProcess>() {
                @Override
                public FlowPcapExportProcess load(String key) {
                    FlowPcapExportProcess result = new FlowPcapExportProcess();
                    result.setProcess(0);
                    return result;
                }
            });

    /**
     * initProcess
     *
     * @param csceneId
     * @return boolean
     */
    public static synchronized boolean initProcess(String csceneId) {
        if (!cache.asMap().containsKey(csceneId)) {
            cache.getUnchecked(csceneId);
            return true;
        } else {
            return false;
        }
    }

    /**
     * updateProcess
     *
     * @param taskId
     * @param process
     */
    public static void updateProcess(String taskId, int process) {
        FlowPcapExportProcess result = cache.getUnchecked(taskId);
        result.setProcess(Math.min(result.getProcess() + process, 95));
    }

    /**
     * getProcess
     *
     * @param taskId
     * @return
     */
    public static FlowPcapExportProcess getProcess(String taskId) {
        FlowPcapExportProcess result = cache.getUnchecked(taskId);
        if (result.getProcess() == 100) {
            new ScheduledThreadPoolExecutor(1).schedule(() -> cache.invalidate(taskId), 5, TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * finalProcess
     *
     * @param taskId
     * @param url
     * @return
     */
    public static FlowPcapExportProcess finalProcess(String taskId, String url) {
        FlowPcapExportProcess result = cache.getUnchecked(taskId);
        result.setProcess(100);
        result.setDownloadUrl(url);
        return result;
    }

    @Data
    public static class FlowPcapExportProcess {
        @ApiModelProperty("文件生成进度")
        private Integer process;
        @ApiModelProperty("文件下载地址")
        private String downloadUrl;
    }
}
