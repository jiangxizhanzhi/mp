package cn.cyberpeace.mp.devopscluster.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 基础类
 */
@Data
@NoArgsConstructor
public class BaseModel implements Serializable {

    private String httpResponse;
    
}
