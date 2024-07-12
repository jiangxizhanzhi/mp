 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneRealEquipmentBo.java.
 // File size 454.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.Data;

/**
 * @ClassName SceneRealEquipmentBo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SceneRealEquipmentBo {

	private String sceneId;

	private String name;

	private String nodeId;

	private Boolean openSyslog;

	private Boolean openKafka;

	private Boolean openRestapi;

	private Boolean openSoap;
}
