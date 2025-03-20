package cn.cyberpeace.mp.behavior.bo;

import lombok.Data;

import java.util.Timer;

@Data
public class BehaviorTimer extends Timer {

	private Boolean running = true;
}
