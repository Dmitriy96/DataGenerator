package by.bsuir.diplom.datageneration.generators;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * Generate date object. Parameter - date in simple date format
 * "yyyy:MM:dd HH:mm:ss".
 * 
 * <p>
 * Parameters: <code>type</code> - operation type: <code>current</code> -
 * generate current date and time. <code>random</code> - generate random date
 * and time between values of the parameters - startDate and endDate. <code>startDate</code>, <code>endDate</code> - parameters for random date generation.
 * <code>dateTime</code> - fixed date value.
 * </p>
 * 
 */

public class DateTimeGenerator implements Generator {

	public static final String DATETIME_PATTERN = "yyyy:MM:dd HH:mm:ss";
	public static final String TYPE_RANDOM = "random";
	public static final String TYPE_CURRENT = "current";
	public static final String TYPE_DEFINED = "defined";
	private String type;
	private String dateTime;
	private String startDate;
	private String endDate;
	private DateFormat format = new SimpleDateFormat(DATETIME_PATTERN);

	/**
	 * Generates object.
	 * 
	 * @return generated object.
	 */

	public Object generate() {

		Date genDateTime;
		if (type.equals(TYPE_DEFINED)) {
			genDateTime = getDate(dateTime);
		} else if (type.equals(TYPE_CURRENT)) {
			genDateTime = new Date();
		} else if (type.equals(TYPE_RANDOM)) {
			genDateTime = getRandomDate(getDate(startDate), getDate(endDate));
		} else {
			genDateTime = new Date();
		}

		return DateFormatUtils.format(genDateTime, DATETIME_PATTERN);
	}

	private Date getDate(String dateString) {
		Date genDateTime;
		try {
			genDateTime = format.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Not correct date format("
					+ DATETIME_PATTERN + "): " + dateString, e);
		}

		return genDateTime;
	}

	private Date getRandomDate(Date startValue, Date endValue) {
		if ((startValue == null) || (endValue == null)) {
			throw new RuntimeException(
					"Inteval for random date generation is not adjusted.");
		}

		Date genDate;
		long startLong = startValue.getTime();
		long endLong = endValue.getTime();
		long interval = endLong - startLong;
		if (interval < 0) {
			throw new RuntimeException(
					"Inteval for random date generation is not correct adjusted.");
		} else if (interval == 0) {
			genDate = new Date(startLong);
		} else {
			genDate = new Date(startLong
					+ (Math.abs(new Random().nextLong()) % interval));
		}

		return genDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
}
