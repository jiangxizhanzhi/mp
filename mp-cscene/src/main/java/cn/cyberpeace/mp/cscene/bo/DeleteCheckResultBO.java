package cn.cyberpeace.mp.cscene.bo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteCheckResultBO {

    public DeleteCheckResultBO(boolean result) {
        this.result = result;
    }

    private boolean result;
}
