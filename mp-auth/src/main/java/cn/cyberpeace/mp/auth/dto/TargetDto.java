package cn.cyberpeace.mp.auth.dto;

import cn.cyberpeace.mp.auth.model.TargetModel;
import lombok.Data;

import java.util.List;

@Data
public class TargetDto extends TargetModel {

    List<TargetDto> children;

}
