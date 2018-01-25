package hime.model;

import interfaces.Interval;


/**
 * Motif interval: Storing basic functions for describing intervals
 * 
 * @author yfeng
 *
 */
public class MotifInterval implements Comparable<MotifInterval>, Cloneable, Interval {

	public long startPos; // interval start
	public long endPos; // interval stop

	public MotifInterval() {
		super();
		this.startPos = -1;
		this.endPos = -1;
	}

	public MotifInterval(long startPos, long endPos) {
		super();
		this.startPos = startPos;
		this.endPos = endPos;
	}

	/**
	 * @param startPos
	 *          starting position within the original time series
	 */
	public void setStart(long startPos) {
		this.startPos = startPos;
	}

	/**
	 * @return starting position within the original time series
	 */
	public long getStart() {
		return startPos;
	}

	/**
	 * @param endPos
	 *          ending position within the original time series
	 */
	public void setEnd(long endPos) {
		this.endPos = endPos;
	}

	/**
	 * @return ending position within the original time series
	 */
	public long getEnd() {
		return endPos;
	}

	public int getLength() {
		return (int) (this.endPos - this.startPos);
	}

	public String toString() {
		return "[" + startPos + "-" + endPos + "]";
	}

	public int compareTo(MotifInterval arg0) {
		return Long.compare(this.getStart(), arg0.getStart());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (endPos ^ (endPos >>> 32));
		result = prime * result + (int) (startPos ^ (startPos >>> 32));
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
		MotifInterval other = (MotifInterval) obj;
		if (endPos != other.endPos)
			return false;
		if (startPos != other.startPos)
			return false;
		return true;
	}

	@Override
	public MotifInterval clone() {
		MotifInterval clone = new MotifInterval();
		clone.startPos = this.startPos;
		clone.endPos = this.endPos;
		return clone;
	}

}