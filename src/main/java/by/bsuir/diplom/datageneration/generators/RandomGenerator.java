package by.bsuir.diplom.datageneration.generators;

import java.util.Random;

/**
 * Generate random string values. No parameters.
 * 
 */
public class RandomGenerator implements Generator {

	public static final int MAX_VALUE = Integer.MAX_VALUE;
	private int maxValue = MAX_VALUE;

	/**
	 * @see com.datageneration.generators.Generator#generate()
	 */
	public Object generate() {
		return Integer.toString(Math.abs(new Random().nextInt(maxValue)));
	}

	/**
	 * gets max value of result number.
	 * 
	 * @return max value of result number.
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * sets max value of result number.
	 * 
	 * @param max max value of result number.
	 */
	public void setMaxValue(int max) {
		this.maxValue = max;
	}

}
