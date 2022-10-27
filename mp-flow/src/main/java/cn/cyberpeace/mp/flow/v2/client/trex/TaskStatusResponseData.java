package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.validation.stereotype.Length;
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/27
 */
@Getter
@Setter
public class TaskStatusResponseData {
    private Integer id;
    private String taskId;
    private Integer status;
    private String portName;
    private Integer portNum;
    private String mac;
    private String createTime;
    private String updateTime;
}
