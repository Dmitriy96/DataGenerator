package by.bsuir.diplom.datageneration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.jcatalog.commons.hibernate.SessionManager;
//import com.jcatalog.commons.hibernate.mapping.MappingObject;

/**
 * DataGenerator that puts generated objects in database using Hibernate.
 * 
 */
public class HibernateDataGenerator extends AbstractDataGenerator {

	private static Log log = LogFactory.getLog(HibernateDataGenerator.class);
	private static InputStream scriptStream = null;
	private static final int TRANSIENT_OBJECTS_COUNT = 1000;
	private LinkedList commands = new LinkedList();
	private Session session = null;
	private int count;

	/**
	 * Create new HibernateDataGenerator.
	 * 
	 * @throws Exception if any error occurs.
	 */
	public HibernateDataGenerator() throws Exception {
//		session = SessionManager.getSession();
		count = 0;
	}

	/**
	 * Fills list of commands (f.e.: delete from Address;) from file.
	 * 
	 * @param filePath path to file with commands for deleting all data from
	 *            database.
	 * 
	 * @throws Exception
	 */
	private void fillCommandsFromFile(String filePath) throws Exception {
		try {
			InputStream stream = ((scriptStream != null) ? scriptStream
					: getClass().getClassLoader().getResourceAsStream(filePath));
			if (stream != null) {
				InputStreamReader isr = new InputStreamReader(stream);
				LineNumberReader lnr = new LineNumberReader(isr);
				String command = null;
				while ((command = lnr.readLine()) != null) {
					commands.addLast(command);
				}
				lnr.close();
				isr.close();
				stream.close();
			}
		} catch (Exception e) {
			log.error("can't read file=" + filePath + ":" + e);
			throw new Exception(e);
		}
	}

	/**
	 * Flush underlying session.
	 * 
	 * @throws Exception if any error occurs
	 */
	public void flush() throws Exception {
		try {
			session.flush();
			session.connection().commit();
		} catch (Exception e) {
			log.error(e);
			session.connection().rollback();
			throw new Exception(e);
		}
	}

	/**
	 * Close underlying session.
	 * 
	 * @throws Exception if any error occurs
	 */
	public void close() throws Exception {
		session.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processCreateObject(String id, Object object)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Storing object " + object + " with id " + id);
		}
//		MappingObject obj = null;
//		try {
//			obj = (MappingObject) processLoadObject(id, object);
//		} catch (Exception e) {
//		}
		boolean isLoaded = false;
//		if (obj != null) {
			isLoaded = true;
//		}
		if (!isLoaded) {
			try {
				session.save(object);
			} catch (MappingException e) {
				if (log.isDebugEnabled()) {
					log.warn("Trying store object " + object
							+ " that not be mapping.");
				}
				return;
			}
		}
		counterHandler();
	}

	private void counterHandler() throws Exception {
		count++;
		if (count > TRANSIENT_OBJECTS_COUNT) {
			count = 0;
			flush();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object processLoadObject(String id, Object object)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Load object " + object + " with id " + id);
		}

//		session.load(object, SessionManager.getSessionFactory()
//				.getClassMetadata(object.getClass()).getIdentifier(object));
		return object;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processDeleteObject(String id, Object object)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Delete object " + object + " with id " + id);
		}

//		session.load(object, SessionManager.getSessionFactory()
//				.getClassMetadata(object.getClass()).getIdentifier(object));
		session.delete(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processUpdateObject(String id, Object object)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Update object " + object + " with id " + id);
		}

		session.saveOrUpdate(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processExecuteScript() throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Delete all data from database using script:" + CLEAR_SQL);
		}

		fillCommandsFromFile(CLEAR_SQL);
		Statement stmt = session.connection().createStatement();
		for (int i = 0; i < commands.size(); i++) {
			stmt.execute((String) commands.get(i));
		}
	}

	public static void setScriptStream(InputStream stream) {
		scriptStream = stream;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void eventFlush() throws Exception {
		count = 0;
		flush();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void eventClear(List objects) throws Exception {
		for (Iterator i = objects.iterator(); i.hasNext();) {
			session.evict(i.next());
		}
		session.disconnect();
		session.reconnect();
	}

	/**
	 * main method.
	 * 
	 * @param args command line arguments
	 * 
	 * @throws Exception if any error occurs
	 */
	public static void main(String[] args) throws Exception {
		// configure hibernate
		if (args.length == 0) {
			System.out
					.println("You should define configuration file as first argument:\n"
							+ "HibernateDataGenerator /conf/test.xml /conf/hibernate.properties\n"
							+ "If second argument is missed then program will try to load hibernate.properies from classpath.");
		}
		if (log.isDebugEnabled()) {
			log.debug("Start data-generator for file:"
					+ new File(args[0]).getAbsolutePath());
		}

		if (args.length > 1) {
//			SessionManager.configure(args[1]);
			log.debug("Use hibernate properties from:"
					+ new File(args[1]).getAbsolutePath());
		}
		for (int i = 0; i < args.length; i++) {
			if ("-p6spy".equals(args[i])) {
				Class.forName("com.p6spy.engine.spy.P6SpyDriver").newInstance();
			}
		}
		HibernateDataGenerator dataGenerator = new HibernateDataGenerator();
		try {
			dataGenerator.generate(new File(args[0]));
			dataGenerator.flush();
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			dataGenerator.close();
		}
	}
}
