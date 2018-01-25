package hime.discretization;

import java.io.BufferedInputStream;


import java.io.BufferedReader;



import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import hime.model.AdaptiveSAX;
import hime.model.HIMEFactory;
import hime.sax.SAXRecord;
import hime.sax.index.BatchSAX;
import interfaces.SAXNode;
import utils.StatUtils;

/**
 * TimeSeriesTokenize is used for streaming read time series and converts the raw data to discrete tokens 
 * 
 * @author yfeng
 */
public class TimeSeriesTokenize {
	double[] buffer;
	
	//SAX Parameters
	int saxWindowSize,saxPAASize,saxAlphabetSize,buff=10000;

	double normalizationThreshold;
	
	//Streaming Read State Variable
	boolean isEnd=false,isStart=true;
	
	//Counting & Performance Measures
	public static long lineC=0;

	public static long startTime=System.nanoTime();
	public static long lineN=0;


	//File Reader
	CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	FileInputStream input;
	BufferedReader reader;
	String files;
	
	//Adaptive SAX Table & Time Series Data
	public static final BatchSAX normalA = new BatchSAX();
	public static double[] timeseries;
	public static double[] x;

	public static double[] x2;

	public double[] xy;
	

	/**
	 * Conduct streaming discertizing process
	 * @return the discretized token sequence
	 * @throws Exception
	 */
	public SAXNode readTokens() throws Exception
	{
		if(isEnd)
			return null;
		try {
			timeseries=this.readTS(buff);		
			if(timeseries.length==0)
				return null;
			return this.discretize(saxWindowSize, saxPAASize, saxAlphabetSize, normalizationThreshold);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public TimeSeriesTokenize(String files, int saxWindowSize, int saxPAASize, int saxAlphabetSize,
			double normalizationThreshold) throws IOException {
		super();
		this.files = files;
		int l=countLines(files); 
		timeseries=new double[l];
		x=new double[l];
		x2=new double[l];
		
		this.saxWindowSize = saxWindowSize;
		this.saxPAASize = saxPAASize;
		this.saxAlphabetSize = saxAlphabetSize;
		this.normalizationThreshold = normalizationThreshold;
		
		Path path = Paths.get(files);
		if (!(Files.exists(path))) {
			try {
				throw new Exception("unable to load data - data source not found.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		buffer=new double[this.saxWindowSize];
    decoder.onMalformedInput(CodingErrorAction.IGNORE);
    input=new FileInputStream(files);
    InputStreamReader readers = new InputStreamReader(input, decoder);
		reader =new BufferedReader(readers);
	}
	
	
	public static int countLines(String filename) throws IOException {
    InputStream is = new BufferedInputStream(new FileInputStream(filename));
    try {
        byte[] c = new byte[1024];
        int count = 0;
        int readChars = 0;
        boolean empty = true;
        while ((readChars = is.read(c)) != -1) {
        
        	empty = false;
            for (int i = 0; i < readChars; ++i) {
                if (c[i] == '\n') {
                    ++count;
                }
            }
        }
        return (count == 0 && !empty) ? 1 : count;
    } finally {
        is.close();
    }
}
	
	public double[] readTS(int loadLimit) throws Exception {
		
		
		try {
			String line = null;
			long lineCounter = 0;
			lineN=lineC;
			while ((line = reader.readLine()) != null) {
				lineC++;
				double value=-1;
				try{
				String[] lineSplit = line.trim().split("\\s+");
				if(lineSplit[0].equals("NaN"))
				{
					value=0;
				}
				else
					value = new BigDecimal(lineSplit[0]).doubleValue();	
				timeseries[(int)lineCounter]=value;
				lineCounter++;
				/*
			if(lineCounter%10000==0)
			  System.out.println(lineCounter);
			  */
				if ((loadLimit > 0) && (lineCounter >= loadLimit)) {
					break;
				}
			}
			catch(Exception e) {
				timeseries[(int)lineCounter]=-1.0;
					lineCounter++;
					if ((loadLimit > 0) && (lineCounter >= loadLimit)) {
						break;
					}
					continue;
				}
			}
		}
		catch (Exception e) {
			System.err.println(e);
		}
		finally {
			assert true;
		}
		if (!(timeseries.length==0)) {
			
			return timeseries;
		}
		isEnd=true;
		return new double[0];
	}
		
	public SAXNode discretize(int saxWindowSize, int saxPAASize,
			int saxAlphabetSize, double normalizationThreshold) throws Exception {

		// scan across the time series extract sub sequences, and convert
		// them to strings
		x=new double[timeseries.length];
		x2=new double[timeseries.length];
		double sum=0,sum2=0;
		int i=0;
		int step=0;
		int paat=32;
		double[] paa_prev=new double[paat];
		double[] paaTest=new double[paat];
		//double[][] paaSet=new double[timeseries.length][32];

		for (i = 0; i < timeseries.length; i=i+1)
		{
			sum=sum+timeseries[i];
			sum2=sum2+timeseries[i]*timeseries[i];
			x[i]=sum;
			x2[i]=sum2;
		}
		
		if(!isStart)
		{
			lineN=lineN-saxWindowSize;
		}
		else
		{
			isStart=false;
		}
		long elapsedTime = System.nanoTime() - TimeSeriesTokenize.startTime;
		System.out.println("Start Adaptive Parameter Selection a: "+elapsedTime / 1.0e9 + " seconds" + " at " + TimeSeriesTokenize.lineC);
		if(HIMEFactory.adaptive)
			determineAlp();
		elapsedTime = System.nanoTime() - TimeSeriesTokenize.startTime;
		System.out.println("Process End: "+elapsedTime / 1.0e9 + " seconds" + " at " + TimeSeriesTokenize.lineC);
		
		//Computing Streaming Linkage for each word
		SAXNode head=StreamingLink();
		SAXNode prev=null;
		for(i=0;i<paa_prev.length;i++){ paa_prev[i]=1000;}
		SAXNode pointer=head;
		i=-1;
		System.out.println("Alp: "+HIMEFactory.a);
		System.out.println("Sliding Window: "+HIMEFactory.ww);
		System.out.println("Paa: "+HIMEFactory.paa);
		while(pointer.neighbor()!=null)
		{
			i++;
			/*
			if(pointer.getLoc()%10000==0)
			  System.out.println(pointer.getLoc());
			  */
		//compute PAA under w=32
			double Ex2=x2[i+saxWindowSize-1]-x2[i]+timeseries[i]*timeseries[i];
			double Ex=x[i+saxWindowSize-1]-x[i]+timeseries[i];
			double sig=Math.sqrt((Ex2-Ex*Ex/saxWindowSize)/(saxWindowSize-1));
			double means=Ex/saxWindowSize;
			step=0;
			int S=saxWindowSize/paat;
			if(sig>0 && !Double.isNaN(sig))
			{
					for(int j=i;j<=i+saxWindowSize-Math.max(S,paat);j=j+S)
					{
							int n=j+S;
							double ExN=x[n-1]-x[j]+timeseries[j];
							paaTest[step]=ExN/(S*sig)-means/sig;
							step++;
					}
			}

			double distPAA=0;
			double tmp=HIMEFactory.ww/paat;
			for(int j=0;j<paat;j++)
			{
				distPAA+=tmp*(paaTest[j]-paa_prev[j])*(paaTest[j]-paa_prev[j]);
			}
			
			distPAA=Math.sqrt(distPAA);
		
			if(distPAA<HIMEFactory.thres*HIMEFactory.ww && pointer.getLoc()-prev.getLoc()<=HIMEFactory.ww-1) {
								pointer=pointer.neighbor();
								continue;
			}
			//if satisfied jump condition
			if(prev==null){
				prev=pointer; //if initial link, assign node for prev
				paa_prev=paaTest;
				paaTest=new double[paat];
				}
			else
			{
				prev.setnext(pointer);
				//assigned all blank nodes
				SAXNode tmpNode=pointer;
				while(prev.neighbor()!=pointer && tmpNode.neighbor()!=null)
				{
					prev=prev.neighbor();
					prev.setnext(tmpNode.neighbor());
					tmpNode=tmpNode.neighbor();
				}
				paa_prev=paaTest;
				paaTest=new double[paat];
			}
			pointer=pointer.neighbor();
		}
		reverselink(head);
		return head;
	}

private void determineAlp() {
		// Choosing parameter a from tightness of lowerbound
	  double est_a=0;
	  int c=0;
	  for(int i=0;i<1000;i++)
	  {
		int start1=randInt(0,timeseries.length-HIMEFactory.ww);
		int start2=randInt(0,timeseries.length-HIMEFactory.ww);
		double rdist=StatUtils.distance(start1, start2, HIMEFactory.ww);
		String[] str1 = normalA.get(ComputePAA(start1,HIMEFactory.ww,HIMEFactory.paa)).clone();
		String[] str2 = normalA.get(ComputePAA(start2,HIMEFactory.ww,HIMEFactory.paa)).clone();
		est_a+=StatUtils.findResolution(str1, str2, rdist);
		c++;
		
	  }
	  
	  HIMEFactory.a=(int)est_a/c;
	  System.out.println("Adaptive Choosing: "+HIMEFactory.a);
	}

private double[] ComputePAA(int i, int ww, int paa) {
	double Ex2=x2[i+saxWindowSize-1]-x2[i]+timeseries[i]*timeseries[i];
	double Ex=x[i+saxWindowSize-1]-x[i]+timeseries[i];
	double sig=Math.sqrt((Ex2-Ex*Ex/saxWindowSize)/(saxWindowSize-1));
	double means=Ex/saxWindowSize;
	int S=saxWindowSize/saxPAASize;
	double[] paax=new double[saxPAASize];
	//compute PAA for SAX word
	int step=0;
	if(sig>0 && !Double.isNaN(sig))
	{
			for(int j=i;j<=i+saxWindowSize-saxPAASize;j=j+S)
			{
					int n=j+S;
					double ExN=x[n-1]-x[j]+timeseries[j];
					paax[step]=ExN/(S*sig)-means/sig;
					step++;
			}
	}
	return paax;
}

public static int randInt(int min, int max) {

  Random rand = new Random();
  int randomNum = rand.nextInt((max - min) + 1) + min;
  return randomNum;
}

ArrayList<SAXNode> ss=new ArrayList<SAXNode>();
	/**
	 * Generating The Bi-linkage data structure (For new algorithm)
	 * @return
	 */
	private SAXRecord StreamingLink() {
		int i=0;
		double Ex2=x2[i+saxWindowSize-1]-x2[i]+timeseries[i]*timeseries[i];
		double Ex=x[i+saxWindowSize-1]-x[i]+timeseries[i];
		double sig=Math.sqrt((Ex2-Ex*Ex/saxWindowSize)/(saxWindowSize-1));
		double means=Ex/saxWindowSize;
		int S=saxWindowSize/saxPAASize;
		double[] paa=new double[saxPAASize];
		//compute PAA for SAX word
		int step=0;
		if(sig>0 && !Double.isNaN(sig))
		{
				for(int j=i;j<=i+saxWindowSize-saxPAASize;j=j+S)
				{
						int n=j+S;
						double ExN=x[n-1]-x[j]+timeseries[j];
						paa[step]=ExN/(S*sig)-means/sig;
						step++;
				}
		}
		//compute PAA under w=32
		step=0;
		//convert to Multi-resolution SAX word
		String[] currentString = normalA.get(paa).clone();
		//create a SAXRecrod item
		SAXRecord h=new SAXRecord(AdaptiveSAX.switchString(currentString,HIMEFactory.a),i);
		//put to adaptive SAX Forest for determing symbol
		SAXNode pointer=h;
		for (i = 1; i < timeseries.length - (saxWindowSize - 1); i=i+1) {
			/*
			if(i%10000==0)
			  System.out.println(i);
			  */
			//compute subsections and paa size
			 Ex2=x2[i+saxWindowSize-1]-x2[i]+timeseries[i]*timeseries[i];
			 Ex=x[i+saxWindowSize-1]-x[i]+timeseries[i];
			 sig=Math.sqrt((Ex2-Ex*Ex/saxWindowSize)/(saxWindowSize-1));
			 means=Ex/saxWindowSize;
			 S=saxWindowSize/saxPAASize;
			 paa=new double[saxPAASize];

			//compute PAA for SAX word
			step=0;
			if(sig>0 && !Double.isNaN(sig))
			{
					for(int j=i;j<=i+saxWindowSize-saxPAASize;j=j+S)
					{
							int n=j+S;
							double ExN=x[n-1]-x[j]+timeseries[j];
							paa[step]=ExN/(S*sig)-means/sig;
							step++;
					}
			}
			//convert to Multi-resolution SAX word
			currentString = normalA.get(paa).clone();
			//create a SAXRecrod item
			SAXRecord sax=new SAXRecord(AdaptiveSAX.switchString(currentString,HIMEFactory.a),i);
			//put to adaptive SAX Forest for determing symbol
			pointer.setneighbor(sax);
			ss.add(pointer);
			pointer=pointer.neighbor();
		}
		
		return h;
	}
	

	private void reverselink(SAXNode head) {
		SAXNode p=null,n;
		p=head;
		while(p.next()!=null)
		{
			n=p.next();
			n.setprev(p);
			p=p.next();
		}
	}

	public void close() throws IOException
	{
		reader.close();
	}

	public int getBuff() {
		return buff;
	}

	public void setBuff(int buff) {
		this.buff = buff;
	}

}