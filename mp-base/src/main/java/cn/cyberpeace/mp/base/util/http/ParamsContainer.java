package cn.cyberpeace.mp.base.util.http;

import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2021/9/9
 */
@Data
public class ParamsContainer {
    private String value;

    public ParamsContainer(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
