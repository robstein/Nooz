package com.nooz.nooz.util;

/**
 * Static class for mapping stories with bubble sizes. Currently bubble sizing
 * is performed with a function of the list of stories size, which essentially
 * returns a certain set percentage of the screen width for the nth story in a k
 * long story list. As such, the bubble size is based on the bubble rank
 * relative to the other stories in the list.
 * 
 * @author Rob Stein
 * 
 */
public class BubbleSizer {

	public static final double SIZE0 = 0.18;
	public static final double SIZE1 = 0.166666667;
	public static final double SIZE2 = 0.131944445;
	public static final double SIZE3 = 0.111111111;
	public static final double SIZE4 = 0.0777777779;

	/**
	 * Returns a bubble radius given its story's placement in the story list,
	 * the story list length, and the width of the map in pixels
	 * 
	 * @param i
	 *            index in story list
	 * @param size
	 *            size of story list
	 * @param mapWidth
	 *            map width in pixels
	 * @return radius of the story in pixels
	 */
	public static double getBubbleSize(int i, int size, double mapWidth) {
		double retval = 0;
		switch (size) {
		case 1:
			switch (i) {
			case 0:
				retval = SIZE2;
				break;
			}
			break;
		case 2:
			switch (i) {
			case 0:
				retval = SIZE2;
				break;
			case 1:
				retval = SIZE3;
				break;
			}
			break;
		case 3:
			switch (i) {
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
			switch (i) {
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
			switch (i) {
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
			switch (i) {
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
			switch (i) {
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
			switch (i) {
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
			switch (i) {
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
			switch (i) {
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
