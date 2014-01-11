package au.com.wp.util;

import au.com.constants.TimeIntervalEnum;

public class EnumHelperClass {

	private static long second = 1000;
	private static long minute = 60 * second;
	private static long hour = 60 * minute;
	private static long day = 24 * hour;
	private static long week = 7 * day;
	
	public static long getMilliseconds(TimeIntervalEnum timeStr) {
		long mills = 0;
		switch(timeStr) {
			case MIN_5 : {
				mills = 5 * minute;
				break;
			}
			
			case MIN_10: {
				mills = 10 * minute;
				break;
			}
			
			case MIN_15 : {
				mills = 15 * minute;
				break;
			}
			
			case MIN_20: {
				mills = 20 * minute;
				break;
			}

			case MIN_30 : {
				mills = 30 * minute;
				break;
			}
			
			case MIN_40: {
				mills = 40 * minute;
				break;
			}

			case MIN_50 : {
				mills = 50 * minute;
				break;
			}
			
			case HR_1: {
				mills = hour;
				break;
			}

			case HR_1_05: {
				mills = hour * (5 * minute);
				break;
			}
			
			case HR_1_10: {
				mills = hour * (10 * minute);
				break;
			}
			
			case HR_1_15: {
				mills = hour * (15 * minute);
				break;
			}
			
			case HR_1_20: {
				mills = hour * (20 * minute);
				break;
			}
			
			case HR_1_30: {
				mills = hour * (30 * minute);
				break;
			}
			
			case HR_1_40: {
				mills = hour * (40 * minute);
				break;
			}
			
			case HR_1_50: {
				mills = hour * (50 * minute);
				break;
			}
			
			case HR_2: {
				mills = 2 * hour;
				break;
			}
			
			case HR_3: {
				mills = 3 * hour;
				break;
			}
			
			case HR_4: {
				mills = 4 * hour;
				break;
			}
			
			case HR_5: {
				mills = 5 * hour;
				break;
			}
			
			case HR_6: {
				mills = 6 * hour;
				break;
			}
			
			case HR_7: {
				mills = 7 * hour;
				break;
			}

			case HR_8: {
				mills = 8 * hour;
				break;
			}
			
			case HR_10: {
				mills = 10 * hour;
				break;
			}
			
			case HR_12: {
				mills = 12 * hour;
				break;
			}
			
			case HR_14: {
				mills = 14 * hour;
				break;
			}
			
			case HR_16: {
				mills = 16 * hour;
				break;
			}
			
			case HR_18: {
				mills = 18 * hour;
				break;
			}
			case HR_20: {
				mills = 20 * hour;
				break;
			}
			
			case HR_22: {
				mills = 22 * hour;
				break;
			}
			
			case DAY_1: {
				mills = day;
				break;
			}
			
			case DAY_2: {
				mills = 2 * day;
				break;
			}
			
			case DAY_3: {
				mills = 3 * day;
				break;
			}
			
			case DAY_4: {
				mills = 4 * day;
				break;
			}

			case DAY_5: {
				mills = 5 * day;
				break;
			}
			
			case DAY_6: {
				mills = 6 * day;
				break;
			}
			
			case DAY_7: {
				mills = week;
				break;
			}
			
			default : {
				mills = 5 * minute;
				break;
			}
		}
		return mills;
	}
}
