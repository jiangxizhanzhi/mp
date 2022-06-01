package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 录屏分页查询返回结果
 * @Author qiaokang
 * @Date：2021/9/13 16:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenrecordPageResponse extends BaseCloudPlatformResult {

    private List<Record> record;

    private Integer totalnum;

    private Integer totalpage;

    private String requestId;

    @Data
    public static final class Record implements Serializable {

        /**
         * 用户名
         */
        private String username;

        /**
         * 组织
         */
        private String department;

        /**
         * 场景
         */
        private String scene;

        /**
         * 录屏文件名
         */
        private String recordingFile;

        /**
         * 截屏文件大小，单位:字节(B)
         */
        private Long fileSize;

        /**
         * 录屏时间
         */
        private String recordate;

        /**
         * 录屏时长
         */
        private Double recortime;
    }

}
