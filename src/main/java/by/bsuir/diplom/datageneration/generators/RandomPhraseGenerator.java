package by.bsuir.diplom.datageneration.generators;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generated random phrase based on the word list.
 * 
 */
public class RandomPhraseGenerator implements Generator {

	private static final Random RANDOM = new Random();
	private static Log log = LogFactory.getLog(RandomPhraseGenerator.class);

	/** default word count in generated phrase */
	public static final int DEFAULT_WORD_COUNT = 5;
	public static final int DEFAULT_PROPERTY_LENGTH = 15;
	private static InputStream wordsStream = null;
	private int wordCount = DEFAULT_WORD_COUNT;
	private int length = DEFAULT_PROPERTY_LENGTH;
	private String words = "one,two,three";
	private String file = null;

	/**
	 * @see com.datageneration.generators.Generator#generate()
	 */
	public Object generate() {
		List wordList = new ArrayList();
		if (file != null) {
			wordList = createWordListFromFile(file);
		} else {
			wordList = creatWordList(words);
		}
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < wordCount; i++) {
			String nextRandomWord = (String) wordList.get(Math.abs(RANDOM
					.nextInt(wordList.size() - 1)));
			if ((result.length() + nextRandomWord.length()) > length) {
				break;
			}
			result.append(nextRandomWord);
			if (i != (wordCount - 1)) {
				result.append(" ");
			}
		}
		return result.toString();

	}

	private List creatWordList(String words) {
		List result = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(words, ",");
		while (tokenizer.hasMoreTokens()) {
			result.add(tokenizer.nextElement());
		}

		return result;
	}

	private List createWordListFromFile(String filePath) {
		List res = new ArrayList();
		try {
			InputStream stream = ((wordsStream != null) ? wordsStream
					: getClass().getClassLoader().getResourceAsStream(filePath));
			if (stream != null) {
				InputStreamReader isr = new InputStreamReader(stream,
						"ISO-8859-1");
				LineNumberReader lnr = new LineNumberReader(isr);
				lnr.setLineNumber(0);
				String word = null;
				while ((word = lnr.readLine()) != null) {
					res.add(word);
				}
				lnr.close();
				isr.close();
				stream.close();
			}
		} catch (Exception e) {
			System.err.println("e");
		}
		return res;
	}

	/**
	 * Return word count in generated phrase.
	 * 
	 * @return word count
	 */
	public int getWordCount() {
		return wordCount;
	}

	/**
	 * Return list of words used in phrase generation.
	 * 
	 * @return word list
	 */
	public String getWords() {
		return words;
	}

	/**
	 * Set word count in generated phrase.
	 * 
	 * @param wordCount word count
	 */
	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	/**
	 * Set list of words used in phrase generation. Words should be separeated
	 * by commas.
	 * 
	 * @param words list of words
	 */
	public void setWords(String words) {
		this.words = words;
	}

	/**
	 * gets file name that contains words for phrase generation.
	 * 
	 * @return name of file with words.
	 */
	public String getFile() {
		return file;
	}

	/**
	 * sets file name that contains words for phrase generation.
	 * 
	 * @param file name of file with words.
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * gets length of result string.
	 * 
	 * @return length of result string.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * sets length of result string.
	 * 
	 * @param length length of result string.
	 */
	public void setLength(int length) {
		this.length = length;
	}

	public static void setWordsStream(InputStream wordsStream) {
		RandomPhraseGenerator.wordsStream = wordsStream;
	}
}
