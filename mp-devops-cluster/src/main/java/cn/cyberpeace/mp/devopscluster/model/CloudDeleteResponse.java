package cn.cyberpeace.mp.devopscluster.model;

import lombok.Data;

@Data
public class CloudDeleteResponse extends BaseModel{
    private Boolean success;
    private String message;

    public boolean successFlag() {
        return Boolean.TRUE.equals(this.getSuccess());
    }
}
