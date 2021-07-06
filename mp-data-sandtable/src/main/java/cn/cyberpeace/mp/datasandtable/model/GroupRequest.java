package cn.cyberpeace.mp.datasandtable.model;

import java.util.List;
import javax.validation.constraints.NotNull;

public class GroupRequest {

    @NotNull(message = "groupId不能为空")
    private String groupId;

    /**
     * 物理机地址
     */
    @NotNull(message = "macIds不能为空")
    private List<String> macIds;

    /**
     * 组的请求地址
     */
    @NotNull(message = "url不能为空")
    private String url;
}
