package cn.cyberpeace.mp.auth.repository.condition;

import cn.cyberpeace.mp.base.repository.condition.CustomCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantCondition extends CustomCondition {

    private String identifier;

    private String name;
}
