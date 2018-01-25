package hime.model;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import interfaces.SAXNode;


/**
 * Motif Set: Storing basic function for describing set of motif
 * 
 * @author yfeng
 *
 */

public class MotifSet {

	private static HashMap<SAXNode, Set<MotifInterval>> table = new HashMap<SAXNode, Set<MotifInterval>>();

	public static Set<MotifInterval> get(Object key) {
		return table.get(key);
	}

	public static boolean containsKey(Object key) {
		return table.containsKey(key);
	}

	public static boolean put(SAXNode s2, MotifInterval value) {

		if (table.containsKey(s2)) {
			if (table.get(s2).isEmpty())
				return true;
			Set<MotifInterval> s = table.get(s2);
			if (s.contains(value))
				return false;
			s.add(value);
			return true;
		} else {
			Set<MotifInterval> s = new HashSet<MotifInterval>();
			s.add(new MotifInterval(s2.getLoc() + 1, s2.getLoc() + s2.getLens()));
			s.add(value);
			table.put(s2, s);
			return true;
		}
	}

	public static void clearTrivial() {
		Set<SAXNode> seed = keySet();
		for (SAXNode s : seed) {
			ArrayList<MotifInterval> ss2 = new ArrayList<MotifInterval>(get(s));
			Collections.sort(ss2);
			Set<MotifInterval> ss3 = get(s);
			long tmp = -HIMEFactory.ww - 1000;
			for (MotifInterval r : ss2) {
				long tmp2 = r.getStart();
				if (tmp2 - tmp < r.getLength()) {
					ss3.remove(r);
					continue;
				}
				tmp = tmp2;
			}
			table.put(s, ss3);
		}

	}

	public static void clear() {
		table.clear();
	}

	public static boolean containsValue(Object value) {
		return table.containsValue(value);
	}

	public static Set<SAXNode> keySet() {
		return table.keySet();
	}

	public static void clearInstances(SAXNode s2) {
		Set<MotifInterval> tmp = table.get(s2);
		tmp.clear();
		tmp.add(new MotifInterval(s2.getLoc() + 1, s2.getLoc() + s2.getLens()));
	}
}
