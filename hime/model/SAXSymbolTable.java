package hime.model;

import java.util.HashMap;
import hime.sax.SAXGuard;
import interfaces.SAXNode;
import utils.StatUtils;

public class SAXSymbolTable {

	/** VLSAXTable (Variable Length SAX Table) for motif discovery **/
	private static HashMap<String, VariableLengthSAX> table = new HashMap<String, VariableLengthSAX>();
	/** Variable for removing covered motif candidate **/
	private static SAXNode tmp_test = null;

	/** Main Function for recursive finding long motif **/
	public static void check(SAXNode s) {
		if (s == null)
			return;
		String key = new String(s.getCharString());
		if (!table.containsKey(key)) {
			VariableLengthSAX vsax = new VariableLengthSAX(key);
			vsax.put(s.getLens(), s);
			table.put(key, vsax);
		} else {

			VariableLengthSAX tmp = table.get(key);
			SAXNode record = tmp.get(s.getLens());
			if (record == null) {
				tmp.put(s.getLens(), s);
				return;
			}
			if (Math.abs(record.getLoc() - s.getLoc()) < record.getLens()) {
				return;
			}

			else {

				boolean noloop = MotifSet.put(record, new MotifInterval(s.getLoc() + 1, s.getLoc() + s.getLens()));
				if (!noloop)
					return;

				SAXGuard g = StatUtils.generateVLSAX(s, HIMEFactory.paa);
				if (g == null)
					return;

				if (tmp_test != null)
					if (MotifSet.get(tmp_test).size() <= 2) {
						MotifSet.clearInstances(tmp_test);
						tmp = table.get(new String(tmp_test.getCharString()));
						tmp.remove(tmp_test);
					}
				tmp_test = record;
				check(g);
				tmp_test = null;
				SAXGuard g2 = StatUtils.generateVLSAX(record, HIMEFactory.paa);
				check(g2);
			}
		}
	}

}
