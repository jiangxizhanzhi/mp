package cn.cyberpeace.mp.sceneplugincloud.util.resourcename;

import lombok.Getter;

import javax.validation.constraints.NotNull;


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
