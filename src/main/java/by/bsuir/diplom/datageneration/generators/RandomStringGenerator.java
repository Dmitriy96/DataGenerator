package by.bsuir.diplom.datageneration.generators;

import org.apache.commons.lang.RandomStringUtils;

/**
 * Generate random strings of specified length. Characters will be chosen from
 * the set of characters specified.
 * 
 */
public class RandomStringGenerator implements Generator {

	/** default length of generated string */
	public static final int DEFAULT_LENGTH = 10;

	private String characterSet = "abcdefghklmnopqrstuxyz";
	private Long length = new Long(DEFAULT_LENGTH);

	/**
	 * @see com.datageneration.generators.Generator#generate()
	 */
	public Object generate() {
		return RandomStringUtils.random(length.intValue(), characterSet);
	}

	/**
	 * Return characterSet
	 * 
	 * @return character set used for generating random strings
	 */
	public String getCharacterSet() {
		return characterSet;
	}

	/**
	 * Return length.
	 * 
	 * @return number of chars in generated random string
	 */
	public Long getLength() {
		return length;
	}

	/**
	 * Set characterSet
	 * 
	 * @param characterSet character set used for generation of random string
	 */
	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}

	/**
	 * Set length
	 * 
	 * @param length number of chars in generated random string
	 */
	public void setLength(Long length) {
		this.length = length;
	}
}
