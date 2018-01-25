package utils;

import hime.discretization.TimeSeriesTokenize;
import hime.model.AdaptiveSAX;
import hime.model.HIMEFactory;
import hime.sax.SAXGuard;
import hime.sax.index.BatchSAX;
import interfaces.SAXNode;

/**
 * Functions for finding motifs
 * 
 * yfeng
 *
 */
public class StatUtils {

	public static double mean(double x, double N) {
		return x / N;
	}

	public static double std(double Ex, double Ex2, double N) {
		return Math.sqrt((Ex2 - Ex * Ex / N) / (N - 1));
	}

	public static double var(double Ex, double Ex2, double N) {
		return (Ex2 - Ex * Ex / N) / (N - 1);
	}

	public static double distance(double Ex, double Ey, double Ex2, double Ey2, double Exy, double N) {
		double sigX = std(Ex, Ex2, N);
		double sigY = std(Ey, Ey2, N);
		if (sigX == 0 || sigY == 0)
			return 100;

		double mX = mean(Ex, N);
		double mY = mean(Ey, N);

		double tmpX = 1 / (sigX * sigX);
		double tmpY = 1 / (sigY * sigY);
		double tmpXY = 2 / (sigX * sigY);

		double p1 = tmpX * (Ex2 - 2 * Ex * mX + N * mX * mX);
		double p2 = tmpY * (Ey2 - 2 * Ey * mY + N * mY * mY);
		double t1 = mY * Ex;
		double t2 = mX * Ey;

		double pp2 = -t1 - t2;
		double pp3 = N * mX * mY;
		double tmp = p1 + p2 - tmpXY * (pp2 + pp3 + Exy);
		if (tmp < 0) {
			// Handling the accuracy error caused by fast computing
			if (Math.abs(tmp) < 0.0000001)
				tmp = Math.abs(tmp);
		}
		return Math.sqrt(p1 + p2 - tmpXY * (pp2 + pp3 + Exy));
	}

	public static double tightness = 0.5;

	public static int findResolution(String[] x1, String[] x2, double rdist) {
		int a = 10;

		double tmp = ComputeSAXMinDist(AdaptiveSAX.switchString(x1, a), AdaptiveSAX.switchString(x2, a), a, rdist);

		if (tmp > tightness) {
			a = BinarySearchA(x1, x2, rdist, 0, a);
		} else {
			a = BinarySearchA(x1, x2, rdist, a + 1, 20);
		}
		return a;
	}

	public static double distance(int start1, int start2, int L) {
		double dist = -1;
		double Exy = 0;
		double Ex = TimeSeriesTokenize.x[start1 + L - 1] - TimeSeriesTokenize.x[start1]
		    + TimeSeriesTokenize.timeseries[start1];
		double Ey2 = TimeSeriesTokenize.x2[start2 + L - 1] - TimeSeriesTokenize.x2[start2]
		    + TimeSeriesTokenize.timeseries[start2] * TimeSeriesTokenize.timeseries[start2];
		double Ey = TimeSeriesTokenize.x[start2 + L - 1] - TimeSeriesTokenize.x[start2]
		    + TimeSeriesTokenize.timeseries[start2];
		double Ex2 = TimeSeriesTokenize.x2[start1 + L - 1] - TimeSeriesTokenize.x2[start1]
		    + TimeSeriesTokenize.timeseries[start1] * TimeSeriesTokenize.timeseries[start1];

		for (int i = 0; i < L; i++) {
			Exy += TimeSeriesTokenize.timeseries[start1 + i] * TimeSeriesTokenize.timeseries[start2 + i];
		}
		dist = distance(Ex, Ey, Ex2, Ey2, Exy, L);
		return dist;
	}

	private static double ComputeSAXMinDist(String x, String x2, int a, double rdist) {

		try {
			double d = BatchSAX.SAXMinDist(x, x2, a);
			return d / rdist;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	private static int BinarySearchA(String[] x1, String[] x2, double rdist, int start2, int end) {

		int start = (end + start2) / 2;
		double value = -1;
		if (start == start2 || start == end) {
			return start;
		} else {
			value = ComputeSAXMinDist(AdaptiveSAX.switchString(x1, start), AdaptiveSAX.switchString(x2, start), start, rdist);
			if (value <= tightness) {
				return BinarySearchA(x1, x2, rdist, start + 1, end);
			} else {
				return BinarySearchA(x1, x2, rdist, start2, start - 1);
			}
		}
	}

	public static SAXGuard generateVLSAX(SAXNode saxsymbol, int saxPAASize) {

		if (saxsymbol == null)
			return null;

		if (saxsymbol.next() == null && saxsymbol.guard() == null)
			return null;

		SAXNode ns = null;
		if (saxsymbol.guard() == null || saxsymbol.isGuard())
			ns = saxsymbol.next();
		else
			ns = saxsymbol.guard();

		if (ns == null)
			return null;

		int end = (int) ns.getLoc() + ns.getLens();
		if (end >= TimeSeriesTokenize.x.length)
			return null;
		int start = (int) saxsymbol.getLoc();
		int saxWindowSize = end - start + 1;
		double Ex2 = TimeSeriesTokenize.x2[end] - TimeSeriesTokenize.x2[start];
		double Ex = TimeSeriesTokenize.x[end] - TimeSeriesTokenize.x[start];
		double sig = Math.sqrt((Ex2 - Ex * Ex / saxWindowSize) / (saxWindowSize - 1));
		double means = Ex / saxWindowSize;
		int S = saxWindowSize / saxPAASize;
		double[] paa = new double[saxPAASize];

		// compute PAA for SAX word
		int step = 0;
		if (sig > 0 && !Double.isNaN(sig)) {
			for (int j = start; j <= start + saxWindowSize - saxPAASize; j = j + S) {
				int n = j + S;
				double ExN = TimeSeriesTokenize.x[n - 1] - TimeSeriesTokenize.x[j] + TimeSeriesTokenize.timeseries[j];
				paa[step] = ExN / (S * sig) - means / sig;
				step++;
			}
		}

		String[] currentString = TimeSeriesTokenize.normalA.get(paa).clone();

		SAXGuard h = new SAXGuard(AdaptiveSAX.switchString(currentString, HIMEFactory.a), start);

		String k = AdaptiveSAX.switchString(currentString, HIMEFactory.a);

		h.setSaxString(k.toCharArray());

		// Update Link
		h.prev = saxsymbol.prev();

		h.next = saxsymbol.next().next();
		h.setLens(saxWindowSize);
		return h;
	}

}
