package cn.cyberpeace.mp.auth.dto;

import cn.cyberpeace.mp.auth.model.OrgModel;
import lombok.Data;

import java.util.List;

@Data
public class OrgDto extends OrgModel {

    List<OrgDto> children;

}
