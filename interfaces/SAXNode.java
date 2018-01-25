package interfaces;

public interface SAXNode {

	/** General Function for SAXRecord and SAXGuard */
	public char[] getCharString();

	public int getIndexes();

	public boolean isGuard();

	public void setSaxString(char[] saxString);

	/** Linklist Data Structure for SAXGuard and SAXRecord */
	public SAXNode next();

	public SAXNode neighbor();

	public SAXNode prev();

	public void setnext(SAXNode s);

	public void setneighbor(SAXNode s);

	public void setprev(SAXNode s);

	/** Functions for location information */
	public boolean check();

	public int getLoc();

	public int getLens();

	public SAXNode guard();

	public void setGuard(SAXNode s);

	public int hashCode();

}
