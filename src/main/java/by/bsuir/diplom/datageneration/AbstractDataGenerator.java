package by.bsuir.diplom.datageneration;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import by.bsuir.diplom.converters.DateConverter;
import by.bsuir.diplom.datageneration.generators.Generator;

/**
 * Abstract data generator can generate some data using xml template file.
 * Impelemnt methods <code>processCreateObject</code>,
 * <code>processLoadObject</code>, <code>processDeleteObject</code>,
 * <code>processUpdateObject</code> and <code>processExecuteScript</code> to
 * create custom data generator. Note: this class not thread safe.
 */
public abstract class AbstractDataGenerator {
	/** path to sql file for deleting all data from database */
	public static String CLEAR_SQL = "resources/clearall.sql";
	private static Log log = LogFactory.getLog(AbstractDataGenerator.class);
	private Map objects = new HashMap();
	private List keyOjects = new ArrayList();
	private ExpressionEvaluator ee = new ExpressionEvaluator();

	private int counterItems = 0;

	/**
	 * Generate some data from xml file.
	 * 
	 * @param file
	 *            file name
	 * 
	 * @throws Exception
	 *             if any error occurs.
	 */
	public void generate(File file) throws Exception {

		log.info("generating data from file " + file);

		ConvertUtils.register(new DateConverter(), Date.class);

		SAXReader reader;
		reader = new SAXReader();
		Document doc = reader.read(file);
		Element root = doc.getRootElement();

		// iterate through child elements of root
		for (Iterator it = root.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();
			if ("object".equals(element.getName())) {
				createAndProcessObject(element);
			} else if ("series".equals(element.getName())) {
				createSeries(element);
			} else if ("variable".equals(element.getName())) {
				createVariable(element);
			} else if ("execute-script".equals(element.getName())) {
				executeSQLScript(element);
			} else if ("flush".equals(element.getName())) {
				eventFlush();
				flushOperation(element);
			}
		}
		System.out.println("end generate: " + new Date());
	}

	/**
	 * Creates variable in context with given type.
	 * 
	 * @param element
	 * 
	 * @throws Exception
	 *             if any error occurs.
	 */
	private void createVariable(Element element) throws Exception {

		String name = element.attributeValue("name");
		String value = element.attributeValue("value");
		Object objectValue = null;

		if (element.attribute("value") != null) {
			if (element.attribute("type") == null) {
				objectValue = new Integer(value);
			} else {
				objectValue = ConvertUtils.convert(value,
						Class.forName(element.attributeValue("type")));
			}
		} else {
			List elements = element.elements("generator");
			if (elements.isEmpty()) {
				throw new Exception("Can't get generate value for variable "
						+ name);
			}

			if (elements.size() != 1) {
				log.warn("More than one element in the value of variable "
						+ name);
			}
			objectValue = generateValue((Element) elements.get(0));
		}
		ee.getContext().put(name, objectValue);
	}

	/**
	 * Create series of objects from &lt;series&gt; element.
	 * 
	 * @param seriesElement
	 * 
	 * @throws Exception
	 *             if any error occurs.
	 */
	private void createSeries(Element seriesElement) throws Exception {
		int count = evalArithmeticAttribute(seriesElement
				.attributeValue("count"));
		if (count <= 0) {
			throw new Exception("Series count must be greater than zero");
		}

		if (log.isDebugEnabled()) {
			log.debug("creating series of objects (count = " + count + ")");
		}
		int from = 0;

		if (seriesElement.attribute("from") != null) {
			from = evalArithmeticAttribute(seriesElement.attributeValue("from"));
		}
		if (from < 0) {
			throw new Exception("Series init value must be positive");
		}

		if (log.isDebugEnabled()) {
			log.debug("creating series of objects (init value = " + from + ")");
		}

		String indexVar = seriesElement.attributeValue("var");
		if (indexVar != null) {
			if (ee.getContext().containsKey(indexVar)) {
				throw new Exception("Variable " + indexVar + " already in use");
			}
		}

		for (int i = from; i < count; i++) {
			if (log.isDebugEnabled()) {
				log.debug("iteration #" + i);
			}

			// sets index variable in jexl context
			if (indexVar != null) {
				ee.getContext().put(indexVar, new Integer(i));
			}
			for (Iterator it = seriesElement.elementIterator("variable"); it
					.hasNext();) {
				Element series = (Element) it.next();
				createVariable(series);
			}
			for (Iterator it = seriesElement.elementIterator("object"); it
					.hasNext();) {
				Element objectElement = (Element) it.next();
				createAndProcessObject(objectElement);
			}
			for (Iterator it = seriesElement.elementIterator("series"); it
					.hasNext();) {
				Element series = (Element) it.next();
				createSeries(series);
			}
			for (Iterator it = seriesElement.elementIterator("flush"); it
					.hasNext();) {
				Element objectElement = (Element) it.next();
				eventFlush();
				flushOperation(objectElement);
			}
		}

		// remove index variable from jexl context
		ee.getContext().remove(indexVar);
	}

	private int evalArithmeticAttribute(String attrValue) {
		int count = Integer.parseInt(String.valueOf(ee
				.evaluateArithmeticExpression(
						String.valueOf(evaluateExpression(attrValue)))
				.intValue()));
		return count;
	}

	private void createAndProcessObject(Element objectElement) throws Exception {
		String id = (String) evaluateExpression(objectElement
				.attributeValue("id"));
		if (id != null) {
			if (objects.containsKey(id)) {
				throw new Exception("Dupicate id " + id);
			}
		}
		if ((objectElement.attribute("action") == null)
				|| objectElement.attributeValue("action").equals("create")) {
			Object object = createEmptyObject(objectElement);
			setObjectProperties(objectElement, object);
			objects.put(id, object);
			processCreateObject(id, object);
		}
		if ((objectElement.attribute("action") != null)
				&& objectElement.attributeValue("action").equals("load")) {
			Object object = createEmptyObject(objectElement);
			setObjectProperties(objectElement, object);
			objects.put(id, object);
			processLoadObject(id, object);
		}
		if ((objectElement.attribute("action") != null)
				&& objectElement.attributeValue("action").equals("delete")) {
			Object object = createEmptyObject(objectElement);
			setObjectProperties(objectElement, object);
			objects.put(id, object);
			processDeleteObject(id, object);
		}
		if ((objectElement.attribute("action") != null)
				&& objectElement.attributeValue("action").equals("update")) {
			String refid = (String) evaluateExpression(objectElement
					.attributeValue("refid"));
			Object object = objects.get(refid);
			setObjectProperties(objectElement, object);
			processUpdateObject(id, object);
		}
	}

	private void executeSQLScript(Element element) throws Exception {
		if (element.attribute("script") != null) {
			CLEAR_SQL = (String) evaluateExpression(element
					.attributeValue("script"));
		}
		processExecuteScript();
	}

	/**
	 * This method used for storing created object
	 * 
	 * @param id
	 *            unique object id
	 * @param object
	 *            object to store
	 */
	protected abstract void processCreateObject(String id, Object object)
			throws Exception;

	/**
	 * This method used for loading data from database to transient object
	 * 
	 * @param id
	 *            unique object id
	 * @param object
	 *            transient object
	 */
	protected abstract Object processLoadObject(String id, Object object)
			throws Exception;

	/**
	 * This method used for deleting object from database
	 * 
	 * @param id
	 *            unique object id
	 * @param object
	 *            object to delete
	 */
	protected abstract void processDeleteObject(String id, Object object)
			throws Exception;

	/**
	 * This method used for updating object
	 * 
	 * @param id
	 *            unique object id
	 * @param object
	 *            object to update
	 */
	protected abstract void processUpdateObject(String id, Object object)
			throws Exception;

	/**
	 * This method used for deleting all data in database
	 */
	protected abstract void processExecuteScript() throws Exception;

	protected abstract void eventFlush() throws Exception;

	protected abstract void eventClear(List objects) throws Exception;

	private void setObjectProperties(Element objectElement, Object object)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("setting properties for object:" + object
					+ ". From element:" + objectElement);
		}

		// set properties
		for (Iterator it = objectElement.elementIterator("property"); it
				.hasNext();) {
			Element property = (Element) it.next();
			String propertyName = property.attributeValue("name");
			Object propertyValue = getPropertyValue(property);
			if (log.isDebugEnabled()) {
				// set property
				log.debug("Set property " + propertyName + " to "
						+ propertyValue);
			}
			BeanUtils.setProperty(object, propertyName, propertyValue);
		}
	}

	private Object createEmptyObject(Element objectElement)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {

		if (log.isDebugEnabled()) {
			log.debug("creating object of class "
					+ objectElement.attributeValue("class"));
		}

		// class
		Class objectClass = Class
				.forName(objectElement.attributeValue("class"));

		// object
		Object object = objectClass.newInstance();
		return object;
	}

	private Object getPropertyValue(Element propertyElement) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("getPropertyValue for "
					+ propertyElement.attributeValue("name"));
		}

		if (propertyElement.attributeValue("value") != null) {
			log.debug("Using value attribute");
			return evaluateExpression(propertyElement.attributeValue("value"));
		}

		String refId = propertyElement.attributeValue("refid");
		if (refId != null) {
			refId = (String) evaluateExpression(refId);
		}
		if (refId != null) {
			log.debug("Using refid attribute");
			// if (!keyOjects.contains(refId)) {
			if (!objects.containsKey(refId)) {
				throw new Exception("Wrong refid " + refId);
			}

			return objects.get(refId);
		}

		List elements = propertyElement.elements("generator");
		if (elements.isEmpty()) {
			throw new Exception("Can't get generate value for property "
					+ propertyElement.attributeValue("name"));
		}
		if (elements.size() != 1) {
			log.warn("More than one element in the value of property "
					+ propertyElement.attributeValue("name"));
		}

		return generateValue((Element) elements.get(0));
	}

	private Object evaluateExpression(String exprString) {
		if (log.isDebugEnabled()) {
			log.debug("evaluateing expression " + exprString);
		}
		try {
			return ee.evaluate(exprString);
		} catch (Exception e) {
			log.error("error evaluateing expression:" + e.getMessage()
					+ exprString);
			return exprString;
		}
	}

	private Object generateValue(Element generatorElement) throws Exception {
		Class generatorClass = Class.forName(generatorElement
				.attributeValue("class"));
		if (!Generator.class.isAssignableFrom(generatorClass)) {
			throw new Exception("Generator class must implement "
					+ Generator.class);
		}

		if (log.isDebugEnabled()) {
			log.debug("Generating object using generator " + generatorClass);
		}

		// create generator and set its properties
		Generator generator = (Generator) generatorClass.newInstance();
		for (Iterator it = generatorElement.elementIterator("param"); it
				.hasNext();) {
			Element paramElement = (Element) it.next();

			String paramName = paramElement.attributeValue("name");
			Object paramValue = evaluateExpression(paramElement
					.attributeValue("value"));
			BeanUtils.setProperty(generator, paramName, paramValue);
		}

		Object result = generator.generate();
		generator = null;

		if (log.isDebugEnabled()) {
			log.debug("Generated value: " + result);
		}

		return result;
	}

	private void flushOperation(Element element) throws Exception {
		List cleanObjects = new ArrayList();
		if (element.attributeCount() == 0 && element.elements().size() == 0) {
			cleanObjects = new ArrayList(objects.values());
			objects.clear();
		} else if (element.elements().size() != 0) {
			for (Iterator it = element.elementIterator("clean"); it.hasNext();) {
				Element clean = (Element) it.next();
				System.out.println("--clean: " + clean.attributeValue("class"));
				flushOneTypeObject(clean, cleanObjects);
			}
		} else {
			flushOneTypeObject(element, cleanObjects);
		}

		eventClear(cleanObjects);
		cleanObjects.clear();

		System.gc();
	}

	private void flushOneTypeObject(Element element, List cleanObjectsList)
			throws Exception {
		Map cleanObjects = new HashMap();

		if (element.attributeValue("class") != null
				&& element.attributeValue("mask") != null) {
			String className = element.attributeValue("class");
			Pattern mask = Pattern.compile(element.attributeValue("mask"));
			for (Iterator i = objects.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				Matcher m = mask.matcher(key);
				Object value = objects.get(key);
				if (value != null
						&& value.getClass().getName().equals(className)
						&& m.matches()) {
					cleanObjects.put(key, value);
				}
			}
		} else if (element.attributeValue("class") != null) {
			String className = element.attributeValue("class");
			for (Iterator i = objects.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				Object value = objects.get(key);
				if (value != null
						&& value.getClass().getName().equals(className)) {
					cleanObjects.put(key, value);
				}
			}
		} else if (element.attributeValue("mask") != null) {
			Pattern mask = Pattern.compile(element.attributeValue("mask"));
			for (Iterator i = objects.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				Matcher m = mask.matcher(key);
				if (m.matches()) {
					cleanObjects.put(key, objects.get(key));
				}
			}
		}

		if (cleanObjects.size() != 0) {
			System.out.println("--map size: " + objects.size());
			for (Iterator i = cleanObjects.keySet().iterator(); i.hasNext();) {
				objects.remove(i.next());
				counterItems++;
			}
			System.out.println("--map size: " + objects.size());
			System.out.println("remove objects: " + counterItems);
		}

		cleanObjectsList.addAll(cleanObjects.values());
	}
}
