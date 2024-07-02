package cn.cyberpeace.mp.devopscluster.model;

import lombok.Data;

/**
 * @ClassName CloudDeleteResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
public class CloudDeleteResponse extends BaseModel{
    private Boolean success;
    private String message;

    public boolean successFlag() {
        return Boolean.TRUE.equals(this.getSuccess());
    }
}
