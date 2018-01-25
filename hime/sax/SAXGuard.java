package hime.sax;

import interfaces.SAXNode;

/**
 * Data Structure to Store Long SAX word node
 * 
 */

public class SAXGuard extends SAXRecord implements SAXNode {

	public SAXGuard(String str, int idx) {
		super(str, idx);
	}

	public SAXGuard(String str, int idx, SAXRecord s1, SAXRecord s2) {
		super(str, idx);
		this.prev = s1;
		this.next = s2;
	}

	public SAXGuard(SAXNode record, int idx, SAXNode s1, SAXNode s2) {

		super(new String(record.getCharString()), idx);
		this.prev = s1;
		this.next = s2;
	}

	public SAXGuard(SAXNode guard) {

		super(new String(guard.getCharString()), guard.getLoc());
		this.prev = guard.prev();
		this.next = guard.next();
	}

	@Override
	public boolean isGuard() {
		return true;
	}

	@Override
	public boolean check() {
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + ":" + this.getLens() + "G";
	}

}
