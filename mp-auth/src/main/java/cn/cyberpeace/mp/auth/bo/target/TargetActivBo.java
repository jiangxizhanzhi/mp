package cn.cyberpeace.mp.auth.bo.target;

import cn.cyberpeace.mp.auth.model.TargetModel;
import lombok.Data;

import java.util.List;


@Data
public class TargetActivBo extends TargetModel {

    
    public static final String NO_AUTH = "no_auth";

    private List<String> active;

    private List<String> edition;

}
