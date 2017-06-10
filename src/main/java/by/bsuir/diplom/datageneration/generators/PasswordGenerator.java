package by.bsuir.diplom.datageneration.generators;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Generate password digest using MD5 algorithm. Password for generating digest
 * sets using method <code>setPassword</code>.
 * 
 */
public class PasswordGenerator implements Generator {

	/** default length of generated string */
	public static final String DEFAULT_VALUE = "password";
	private String password;

	/**
	 * @see Generator#generate()
	 */
	public Object generate() {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Can't find md5 algorithm", e);
		}
		md5.update(password == null ? DEFAULT_VALUE.getBytes() : password
				.getBytes());
		return toHex(md5.digest());

	}

	private String toHex(byte[] bs) {
		StringBuffer result = new StringBuffer(bs.length * 2);
		for (int i = 0; i < bs.length; i++) {
			result.append(Character.forDigit((0xF0 & bs[i]) >> 4, 16));
			result.append(Character.forDigit(0x0F & bs[i], 16));
		}
		return result.toString();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static void main(String[] str) {
		PasswordGenerator pg = new PasswordGenerator();
		pg.setPassword(str[0]);
		System.out.println(str[0] + " --> [MD5] --> " + pg.generate());
	}
}
