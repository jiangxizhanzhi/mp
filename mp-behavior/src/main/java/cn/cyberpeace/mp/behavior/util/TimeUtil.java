package cn.cyberpeace.mp.behavior.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeUtil {

	public static String format(LocalTime localTime) {
		if (localTime == null) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		return localTime.format(formatter);
	}

	public static LocalTime toLocalTime(String time) {
		if (StringUtils.isEmpty(time)) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		try {
			return LocalTime.parse(time, formatter);
		}catch (DateTimeParseException e) {
			System.out.println("Error parsing time: " + e.getMessage());
			throw new MpRuntimeException("时间[" + time + "]格式化错误");
		}
	}
}
