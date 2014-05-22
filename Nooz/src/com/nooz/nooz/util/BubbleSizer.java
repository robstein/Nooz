package com.nooz.nooz.util;

public class BubbleSizer {

	public static final double SIZE0 = 0.18;
	public static final double SIZE1 = 0.166666667;
	public static final double SIZE2 = 0.131944445;
	public static final double SIZE3 = 0.111111111;
	public static final double SIZE4 = 0.0777777779;
	
	public static double getBubbleSize(int i, int size, double mapWidth) {
		double retval = 0;
		switch(size) {
		case 1:
			switch(i) {
			case 0:
				retval = SIZE2;
				break;
			}
			break;
		case 2:
			switch(i) {
			case 0:
				retval = SIZE2;
				break;
			case 1:
				retval = SIZE3;
				break;
			}
			break;
		case 3:
			switch(i) {
			case 0:
				retval = SIZE1;
				break;
			case 1:
				retval = SIZE2;
				break;
			case 2:
				retval = SIZE3;
				break;
			}
			break;
		case 4:
			switch(i) {
			case 0:
				retval = SIZE1;
				break;
			case 1:
				retval = SIZE2;
				break;
			case 2:
				retval = SIZE3;
				break;
			case 3:
				retval = SIZE4;
				break;
			}
			break;
		case 5:
			switch(i) {
			case 0:
				retval = SIZE1;
				break;
			case 1:
				retval = SIZE2;
				break;
			case 2:
				retval = SIZE3;
				break;
			case 3:
				retval = SIZE4;
				break;
			case 4:
				retval = SIZE4;
				break;
			}
			break;
		case 6:
			switch(i) {
			case 0:
				retval = SIZE1;
				break;
			case 1:
				retval = SIZE2;
				break;
			case 2:
				retval = SIZE3;
				break;
			case 3:
				retval = SIZE3;
				break;
			case 4:
				retval = SIZE4;
				break;
			case 5:
				retval = SIZE4;
				break;
			}
			break;
		case 7:
			switch(i) {
			case 0:
				retval = SIZE1;
				break;
			case 1:
				retval = SIZE2;
				break;
			case 2:
				retval = SIZE3;
				break;
			case 3:
				retval = SIZE3;
				break;
			case 4:
				retval = SIZE4;
				break;
			case 5:
				retval = SIZE4;
				break;
			case 6:
				retval = SIZE4;
				break;
			}
			break;
		case 8:
			switch(i) {
			case 0:
				retval = SIZE1;
				break;
			case 1:
				retval = SIZE2;
				break;
			case 2:
				retval = SIZE2;
				break;
			case 3:
				retval = SIZE3;
				break;
			case 4:
				retval = SIZE3;
				break;
			case 5:
				retval = SIZE4;
				break;
			case 6:
				retval = SIZE4;
				break;
			case 7:
				retval = SIZE4;
				break;
			}
			break;
		case 9:
			switch(i) {
			case 0:
				retval = SIZE1;
				break;
			case 1:
				retval = SIZE2;
				break;
			case 2:
				retval = SIZE2;
				break;
			case 3:
				retval = SIZE3;
				break;
			case 4:
				retval = SIZE3;
				break;
			case 5:
				retval = SIZE4;
				break;
			case 6:
				retval = SIZE4;
				break;
			case 7:
				retval = SIZE4;
				break;
			case 8:
				retval = SIZE4;
				break;
			}
			break;
		case 10:
			switch(i) {
			case 0:
				retval = SIZE0;
				break;
			case 1:
				retval = SIZE1;
				break;
			case 2:
				retval = SIZE2;
				break;
			case 3:
				retval = SIZE2;
				break;
			case 4:
				retval = SIZE3;
				break;
			case 5:
				retval = SIZE3;
				break;
			case 6:
				retval = SIZE4;
				break;
			case 7:
				retval = SIZE4;
				break;
			case 8:
				retval = SIZE4;
				break;
			case 9:
				retval = SIZE4;
				break;
			}
			break;
		}
		return retval * mapWidth / 3;
	}
	
}
