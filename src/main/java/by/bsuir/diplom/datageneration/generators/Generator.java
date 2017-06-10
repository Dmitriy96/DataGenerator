package by.bsuir.diplom.datageneration.generators;

/**
 * This interface used for generating property value for &lt;generator&gt;
 * element of the datageneration xml document. Implement this interface to
 * create custom generator. Properties defined in &lt;param&gt; element set
 * using JavaBean API.
 * 
 */
public interface Generator {
	/**
	 * Generates object.
	 * 
	 * @return generated object.
	 */
	Object generate();

}
