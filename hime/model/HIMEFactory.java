package hime.model;

import hime.discretization.TimeSeriesTokenize;
import hime.sax.SAXGuard;
import interfaces.SAXNode;
import utils.StatUtils;


/**
 * Main Function for HIME 
 * 
 * 
 * @author yfeng
 * 
 */

public final class HIMEFactory {

  public int countsum=0, countsuccess=0;
	public static int ww,CHUNK_SIZE=100000000;
	public static double thres;
 
  /**
   * Disabling the constructor.
   */
  private HIMEFactory() {
    assert true;
  }
 
  
  public static long lens=0;
	public static int paa;
	public static int a;
	public static boolean adaptive=false;
	
  /**
   * Run HIME algroithm after 
   * Step 1: Create the induction graph.
   * Step 2: Run HIME algorithm
   * Step 3: Result is stored in MotifSet class for post-processing
   * 
   * @param 
   * 
   * datafile: File name
   * paa: paa segments number
   * alp: alpbeta size for SAX
   * w: Minimum enumeration length 
   * 
   * @throws Exception
   */
  public static void runHIME(String datafile,int paa,int alp,int w) throws Exception {

    // clear global collections
    //
  	ww=w;
    
    TimeSeriesTokenize tokens=new TimeSeriesTokenize(datafile,w,paa, alp,0.05);
    tokens.setBuff(CHUNK_SIZE);
    SAXNode head;

			head=tokens.readTokens();
			System.gc();
    	if(head.next()==null)
    		return;
    	@SuppressWarnings("unused")
			int currentPosition=0;
    	SAXNode pointer=head;
			while(pointer.next()!=null)
			{
				//System.out.println("position: "+pointer.getLoc());
				lens+=1;
				SAXNode saxsymbol=pointer;
				if(pointer.getLoc()%100000==0)
				{
					MotifSet.clearTrivial();
				  long elapsedTime = System.nanoTime() - TimeSeriesTokenize.startTime;
					System.out.println(elapsedTime / 1.0e9 + " seconds" + " at " + pointer.getLoc());
				}
			//Forms Longer SAX word
			SAXGuard s=StatUtils.generateVLSAX(saxsymbol,paa);
			SAXSymbolTable.check(s);
			pointer=pointer.neighbor();
			
    }
			long elapsedTime = System.nanoTime() - TimeSeriesTokenize.startTime;
			System.out.println(elapsedTime / 1.0e9 + " seconds" + " at " + TimeSeriesTokenize.lineC);
			
 }
}