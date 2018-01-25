import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import hime.model.HIMEFactory;
import hime.model.MotifInterval;
import hime.model.MotifSet;
import interfaces.SAXNode;
import utils.StatUtils;

public class Run {

	/**
	 * Main Class for Hierarchical based Motif Enumeration
	 * 
	 * 
	 * @author yfeng
	 * 
	 */

	/** Default input **/
	public static int paa = 4, a = 5, x = 300;
	public static String INPUT_FILE = "demo.txt";
	public static String dir = "";

	public static void main(String[] args) throws Exception {

		// HIME settings
		HIMEFactory.thres = 0.04;
		HIMEFactory.adaptive = true;
		Parseinput(args);
		HIMEFactory.paa = paa;
		HIMEFactory.ww = x;
		HIMEFactory.a = a;

		// System.out.println("Alp: "+HIMEFactory.a);
		HIMEFactory.runHIME(dir + INPUT_FILE, paa, a, x);
		writeMotifSet();

	}

	/**
	 * Parsing input string
	 * 
	 * @param args
	 * 
	 */
	private static void Parseinput(String[] args) {
		if (args.length == 1)
			INPUT_FILE = args[0];
		if (args.length == 2) {
			INPUT_FILE = args[0];
			x = Integer.parseInt(args[1]);
		}
		if (args.length == 3) {
			INPUT_FILE = args[0];
			paa = Integer.parseInt(args[1]);
			x = Integer.parseInt(args[2]);
		}
		if (args.length == 4) {
			INPUT_FILE = args[0];
			paa = Integer.parseInt(args[1]);
			x = Integer.parseInt(args[2]);
			a = Integer.parseInt(args[3]);
			HIMEFactory.adaptive = false;
		}
		if (args.length == 5) {
			INPUT_FILE = args[0];
			paa = Integer.parseInt(args[1]);
			x = Integer.parseInt(args[2]);
			a = Integer.parseInt(args[3]);
			HIMEFactory.thres = Double.parseDouble(args[4]);
		}

	}

	private static void writeMotifSet() {
		Set<SAXNode> seed = MotifSet.keySet();
		int i = 1;
		for (SAXNode s : seed) {
			ArrayList<MotifInterval> ss2 = new ArrayList<MotifInterval>(MotifSet.get(s));
			Collections.sort(ss2);
			ArrayList<MotifInterval> sx = new ArrayList<MotifInterval>();
			long tmp = -HIMEFactory.ww - 1000;
			for (MotifInterval r : ss2) {
				long tmp2 = r.getStart();
				if (tmp2 - tmp < r.getLength()) {
					continue;
				}
				sx.add(r);
				tmp = tmp2;
			}
			i++;
			double dmin = 1000000;
			int p1 = 0;
			int p2 = 0;
			for (int c = 0; c < sx.size(); c++) {
				for (int c2 = c + 1; c2 < sx.size(); c2++) {
					double d = StatUtils.distance((int) sx.get(c).getStart(), (int) sx.get(c2).getStart(),
					    Math.min(sx.get(c).getLength(), sx.get(c2).getLength()));
					if (dmin > d) {
						dmin = d;
						p1 = c;
						p2 = c2;
					}
				}
			}

			if (dmin >= 1000000)
				continue;

			int l = Math.min(sx.get(p1).getLength(), sx.get(p2).getLength());

			if (i % 10000 == 0)
				System.out.println(i + " Out of " + seed.size());
			System.out.println("Motif: " + sx.get(p1).getStart() + " " + sx.get(p1).getEnd() + " " + sx.get(p2).getStart()
			    + " " + sx.get(p2).getEnd() + " " + l + " " + dmin);
		}
	}

}
