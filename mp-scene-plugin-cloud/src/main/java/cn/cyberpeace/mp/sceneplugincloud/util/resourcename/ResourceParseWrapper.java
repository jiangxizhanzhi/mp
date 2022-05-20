package cn.cyberpeace.mp.sceneplugincloud.util.resourcename;

import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/18
 */
@Getter
public class ResourceParseWrapper<T extends RsourceNameParams> {
    private boolean success;
    private T result;

    public ResourceParseWrapper() {
        this.success = false;
    }

    public ResourceParseWrapper(@NotNull T result) {
        this.result = result;
        this.success = true;
    }
}
