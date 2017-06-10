package by.bsuir.diplom.datageneration.generators;

/**
 * Generate numbers starting from 1 sequentially. Note: This class very ugly and
 * become depracated as soon as EL will be introduced.
 * 
 */
public class SequenceGenerator implements Generator {

	private static int counter = 1;

	/**
	 * Generate numbers starting with 1.
	 * 
	 * @return generated number.
	 */
	public Object generate() {
		return Integer.toString(counter++);
	}

}
