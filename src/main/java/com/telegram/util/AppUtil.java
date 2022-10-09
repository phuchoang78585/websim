package com.telegram.util;

public class AppUtil {

	public static String toStringStatus(boolean status) {
		if(!status) {
			return "Disable";
		}
		return "Enable";
	}

}
