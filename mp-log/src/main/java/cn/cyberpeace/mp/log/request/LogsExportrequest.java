package cn.cyberpeace.mp.log.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("导出日志请求,多个module和userNameOrOpTarget")
public class LogsExportrequest extends LogExportRequest {

    private Integer[] modules;

    //用户名或者是操作对象
    private String userNameOrOpTarget;

}
