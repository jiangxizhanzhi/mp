package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceOsServiceModel implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * binary
     */
    private String binary;

    /**
     * host
     */
    private String host;

    /**
     * zone
     */
    private String zone;

    /**
     * status
     */
    private String status;

    /**
     * state
     */
    private String state;

    /**
     *
     */
    private String disabledReason;

    /**
     *
     */
    private String updatedAt;
}
