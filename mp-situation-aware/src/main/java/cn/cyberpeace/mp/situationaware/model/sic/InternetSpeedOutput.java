package cn.cyberpeace.mp.situationaware.model.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author hewei
 * @Date：2022/7/5 16:00
 */
@Data
public class InternetSpeedOutput {

    @JSONField(name = "DownLoadSpeed")
    private String downLoadSpeed;

    @JSONField(name = "UploadSpeed")
    private String uploadSpeed;
}
