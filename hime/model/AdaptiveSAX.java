package hime.model;

public class AdaptiveSAX {

	/**
	 * A simple class contains function get actual SAX word based on
	 * Multi-Resolution SAX
	 * 
	 * @author yfeng
	 */

	public static String switchString(String[] key, int level) {
		char[] tmp = new char[key.length];
		level = level - 1;
		for (int i = 0; i < key.length; i++) {
			tmp[i] = key[i].charAt(level);
		}
		return String.valueOf(tmp);
	}

}
