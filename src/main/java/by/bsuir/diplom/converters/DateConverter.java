package by.bsuir.diplom.converters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * <p>
 * Standard {@link Converter} implementation that converts an incoming String
 * into a <code>java.util.Date</code> object, optionally using a default value
 * or throwing a {@link ConversionException} if a conversion error occurs.
 * </p>
 */
public final class DateConverter implements Converter {

	public static final String DATETIME_SIMPLE_PATTERN = "MM/dd/yyyy";
	public static final String DATETIME_PATTERN = "yyyy:MM:dd HH:mm:ss";
	private DateFormat simpleFormat = new SimpleDateFormat(DATETIME_SIMPLE_PATTERN);
	private DateFormat format = new SimpleDateFormat(DATETIME_PATTERN);

	// ----------------------------------------------------- Instance Variables

	/** The default value specified to our Constructor, if any. */
	private Object defaultValue = null;

	/** Should we return the default value on conversion errors? */
	private boolean useDefault = true;

	// ----------------------------------------------------------- Constructors

	/**
	 * Create a {@link Converter} that will throw a {@link ConversionException}
	 * if a conversion error occurs.
	 */
	public DateConverter() {
		this.defaultValue = null;
		this.useDefault = false;
	}

	/**
	 * Create a {@link Converter} that will return the specified default value
	 * if a conversion error occurs.
	 * 
	 * @param defaultValue The default value to be returned
	 */
	public DateConverter(Object defaultValue) {
		this.defaultValue = defaultValue;
		this.useDefault = true;
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Convert the specified input object into an output object of the specified
	 * type.
	 * 
	 * @param type Data type to which this value should be converted
	 * @param value The input value to be converted
	 * 
	 * @return
	 * @exception ConversionException if conversion cannot be performed successfully
	 */
	public Object convert(Class type, Object value) {
		if (value == null) {
			if (useDefault) {
				return (defaultValue);
			} else {
				throw new ConversionException("No value specified");
			}
		}

		try {
			if (((String) value).indexOf("/") == -1) {
				return format.parse((String) value);
			} else {
				return simpleFormat.parse((String) value);
			}

		} catch (Exception e) {
			if (useDefault) {
				return (defaultValue);
			} else {
				throw new ConversionException(e);
			}
		}
	}
}
