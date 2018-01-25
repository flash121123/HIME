package hime.model;

import java.util.Collection;

import java.util.HashMap;
import java.util.Set;
import interfaces.SAXNode;

/**
 * 
 * SAX data structure for storing variable length SAX
 * 
 * @author ygao12
 *
 */
public class VariableLengthSAX {

	public String sax;
	public HashMap<Integer, SAXNode> len2record = new HashMap<Integer, SAXNode>();

	public VariableLengthSAX(String sax) {
		super();
		this.sax = sax;
	}

	public String getSax() {
		return sax;
	}

	public void setSax(String sax) {
		this.sax = sax;
	}

	public int size() {
		return len2record.size();
	}

	public boolean isEmpty() {
		return len2record.isEmpty();
	}

	public SAXNode get(Integer key) {
		int gap = key / 10;
		SAXNode s = null;
		int l = 50;
		gap = Math.min(gap, l);

		for (int i = key; i < key + gap; i++) {
			if (len2record.containsKey(i)) {
				l = i - key;
				s = len2record.get(i);
				break;
			}
		}
		if (l != 0)
			for (int i = key - 1; i > key - gap; i--) {
				if (len2record.containsKey(i)) {
					if (l > key - i) {
						s = len2record.get(i);
						l = key - i;
					}
				}
			}

		return s;
	}

	public boolean containsKey(Object key) {
		return len2record.containsKey(key);
	}

	public SAXNode put(Integer key, SAXNode s) {
		return len2record.put(key, s);
	}

	public Set<Integer> keySet() {
		return len2record.keySet();
	}

	public Collection<SAXNode> values() {
		return len2record.values();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sax == null) ? 0 : sax.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableLengthSAX other = (VariableLengthSAX) obj;
		if (sax == null) {
			if (other.sax != null)
				return false;
		} else if (!sax.equals(other.sax))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + len2record.size() + ")" + "[" + len2record.keySet().toString() + "]";
	}

	public void remove(SAXNode tmp_test) {
		// TODO Auto-generated method stub
		len2record.remove(tmp_test.getLens());
	}
}
