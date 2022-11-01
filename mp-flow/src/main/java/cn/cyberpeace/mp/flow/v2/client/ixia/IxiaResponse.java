package cn.cyberpeace.mp.flow.v2.client.ixia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IxiaResponse {
    private String error;

    public String getMessage() {
        return "";
    }

    public String getDetail() {
        return "";
    }

    public void setRequestError() {
    }

    public void setParseError() {
    }

    public boolean isSuccess() {
        return true;
    }
}
