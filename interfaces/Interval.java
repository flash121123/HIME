package interfaces;

public interface Interval {

	public void setStart(long startPos);

	/**
	 * @return starting position within the original time series
	 */
	public long getStart();

	/**
	 * @param endPos
	 *          ending position within the original time series
	 */
	public void setEnd(long endPos);

	/**
	 * @return ending position within the original time series
	 */
	public long getEnd();

}
