package cn.cyberpeace.mp.agent.netconf.model;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("代理执行结果信息")
public class AgentResultBo implements SnakeHttpData, Serializable {

    @ApiModelProperty("代理编号")
    private String agentId;

    @ApiModelProperty("代理类型")
    private String agentType;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("配置任务进度")
    private Integer taskProcess;

    @ApiModelProperty("下发任务ID")
    private String taskId;

    private String message;

    private List<String> inputAndOutput;


    public static <T extends BasicAccessInfo> AgentResultBo enterExecSuccess(TaskRequest<T> taskRequest) {
        AgentResultBo agentResultBo = new AgentResultBo();
        agentResultBo.setTaskId(taskRequest.getTaskId());
        agentResultBo.setStatus(Status.CONNECTING_TARGET);
        agentResultBo.setTaskProcess(10);
        agentResultBo.setMessage("start connect target");
        return agentResultBo;
    }

    public static <T extends BasicAccessInfo> AgentResultBo connectTargetSuccess(TaskRequest<T> taskRequest) {
        AgentResultBo agentResultBo = new AgentResultBo();
        agentResultBo.setTaskId(taskRequest.getTaskId());
        agentResultBo.setStatus(Status.EXECUTING_COMMAND);
        agentResultBo.setTaskProcess(20);
        agentResultBo.setMessage("end connect target, connect success");
        return agentResultBo;

    }

    public static <T extends BasicAccessInfo> AgentResultBo connectTargetFailed(TaskRequest<T> taskRequest, String errorMessage) {
        AgentResultBo agentResultBo = new AgentResultBo();
        agentResultBo.setTaskId(taskRequest.getTaskId());
        agentResultBo.setStatus(Status.FAIL_FINISH);
        agentResultBo.setTaskProcess(30);
        agentResultBo.setMessage(errorMessage);
        return agentResultBo;

    }

    public static <T extends BasicAccessInfo> AgentResultBo execCommandSuccess(TaskRequest<T> taskRequest, List<String> inputAndOutput) {
        AgentResultBo agentResultBo = new AgentResultBo();
        agentResultBo.setTaskId(taskRequest.getTaskId());
        agentResultBo.setStatus(Status.SUCCESS_FINISH);
        agentResultBo.setTaskProcess(100);
        agentResultBo.setMessage("executing command success");
        return agentResultBo;

    }

    public static <T extends BasicAccessInfo> AgentResultBo execCommandFailed(TaskRequest<T> taskRequest, List<String> inputAndOutput, String errorMessage) {
        AgentResultBo agentResultBo = new AgentResultBo();
        agentResultBo.setTaskId(taskRequest.getTaskId());
        agentResultBo.setStatus(Status.FAIL_FINISH);
        agentResultBo.setTaskProcess(40);
        agentResultBo.setMessage(errorMessage);
        return agentResultBo;

    }


    public static class Status {
        public static final int SUCCESS_FINISH = 0;
        public static final int EXECUTING_COMMAND = 1;
        public static final int FAIL_FINISH = 2;
        public static final int CONNECTING_TARGET = 3;
    }

}