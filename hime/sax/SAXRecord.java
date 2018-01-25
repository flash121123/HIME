package hime.sax;

import java.util.Arrays;

import hime.model.HIMEFactory;
import interfaces.SAXNode;

/**
 * Data Structure to Store SAX that with length equal to minimum length
 * 
 */
public class SAXRecord implements SAXNode {

	private char[] saxString = null;
	private int occurrences = -1;

	public SAXRecord() {
		super();
	}

	public SAXRecord(String str, int idx) {

		this.saxString = str.toCharArray();
		this.occurrences = idx;
		this.lens = HIMEFactory.ww;
	}

	public int lens = -1;

	public int getLens() {
		return lens;
	}

	public void setLens(int lens) {
		this.lens = lens;
	}

	/**
	 * Gets the char string stored in the node.
	 * 
	 * @return The string.
	 */
	public char[] getCharString() {
		return this.saxString;
	}

	/**
	 * Get all indexes.
	 * 
	 * @return all indexes.
	 */
	public int getIndexes() {
		return this.occurrences;
	}

	@Override
	public String toString() {
		String p = new String(this.getCharString());
		return p + "," + this.occurrences;
	}

	public char[] getSaxString() {
		return saxString;
	}

	public void setSaxString(char[] saxString) {
		this.saxString = saxString;
	}

	public boolean isGuard() {
		return false;
	}

	public SAXNode neighbor = null, next = null, prev = null, guard = null;

	public int getLoc() {

		return occurrences;
	}

	@Override
	public SAXNode next() {
		return this.next;
	}

	@Override
	public SAXNode neighbor() {
		return this.neighbor;
	}

	@Override
	public SAXNode prev() {
		return this.prev;
	}

	@Override
	public boolean check() {

		return true;
	}

	@Override
	public SAXNode guard() {
		return this.guard;
	}

	@Override
	public void setGuard(SAXNode s) {
		this.guard = s;
	}

	@Override
	public void setnext(SAXNode s) {
		this.next = s;
	}

	@Override
	public void setneighbor(SAXNode s) {
		this.neighbor = s;
	}

	@Override
	public void setprev(SAXNode s) {
		this.prev = s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lens;
		result = prime * result + occurrences;
		result = prime * result + Arrays.hashCode(saxString);
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
		SAXRecord other = (SAXRecord) obj;
		if (lens != other.lens)
			return false;
		if (occurrences != other.occurrences)
			return false;
		if (!Arrays.equals(saxString, other.saxString))
			return false;
		return true;
	}

}