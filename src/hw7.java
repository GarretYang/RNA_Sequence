import java.io.*;
import java.util.*;

// The OPT table for this program used the slide convention
public class hw7 {
	public static void main(String[] args) throws FileNotFoundException, IOException{	
        if(args.length == 0) {
        	System.out.println("Incorrect File Input");
        	System.exit(1);
        } 
        
        // by setting printOut to true, the result will be print to out.txt
        // by setting printOut to true, the result will be print to the console
        boolean printOut = false;
		PrintStream console = System.out;
		if (printOut == true) {
	        PrintStream out = new PrintStream(new File("F:\\ebook\\CSE417\\HW\\HW7\\SUBMIT\\out.txt")); 	
	        System.setOut(out);
		}        
        
        // Take inputs from the commanline
//        for (String fileName: args) {
//        	File file = new File(fileName);
//			Scanner input = new Scanner(file);
//			
//			while (input.hasNextLine()) {
//				String line = input.nextLine();
//				printResult(line);
//			}			         
//        }
        
        System.setOut(console);
        
        // The program would run the timing test on random sequences if the 
        // timeTest boolean is true
		boolean timeTest = true;
		if(timeTest) {
			for (int k = 4; k <= 12; k++) {
				System.out.println("The length is " + (int)Math.pow(2, k));
				timeTest(k);
				timeTest(k);
				timeTest(k);
				System.out.println();
			}
		}        
	}
	
	// This method prints the result information computed by the Nussinov algorithm and the 
	// corresponding trace back.
	public static void printResult(String line) {
		
		int n = line.length();
		
		char[] seq = (' ' + line).toCharArray();
		char[] pairedSeq = new char[seq.length];
		
		long OPTstartTime =  System.nanoTime();				
		int[][] OPTmatrix = nussinov(seq);
		long OPTendTime =  System.nanoTime();				
		
		traceback(1,n,OPTmatrix,seq,pairedSeq);
		System.out.println(line);
		
		for (int i = 1; i < pairedSeq.length; i++) {
			System.out.print(pairedSeq[i]);
		}
		System.out.println();
		System.out.printf("Length = %d, Pairs = %d, Time = %.5f sec" ,n,OPTmatrix[1][n],((OPTendTime - OPTstartTime) / 1000000000.0));
		System.out.println();		
		printMatrix(OPTmatrix);
		System.out.println();	
	}
	
	// This method is used for time tests
	// It prints the Nussinov algorithm and trace back runtime
	// of random RNA sequences
	public static void timeTest(int k) {
		char[] randomSequence = randomSeq(k);
		
		long nussStartTime = System.nanoTime();				
		int[][] OPTmatrix = nussinov(randomSequence);
		long nussEndTime = System.nanoTime();		
		char[] dummy = new char[randomSequence.length];
		long traceStartTime =  System.nanoTime();
		traceback(1,randomSequence.length-1,OPTmatrix,
				  randomSequence,dummy);
		long traceEndTime =  System.nanoTime();	
		
		System.out.printf("The Nussinov algorithm took %.8f sec | The trace back took  %.8f sec \n" ,
						((nussEndTime - nussStartTime) / 1000000000.0),((traceEndTime - traceStartTime) / 1000000000.0));	
	}													 
	
	// This method output the the OPT table for the RNA pairs
	// The table used the convention of the lecture slide
	public static int[][] nussinov(char[] seq) {
		// append the array to index base 1
		int length = seq.length - 1;
		int[][] OPTmatrix = new int[length+1][length+1];
		for (int k = 5; k <= length - 1; k++) {
			for (int i = 1; i <= length - k; i++) {
				int j = i + k;
				int unpairedResult = OPTmatrix[i][j-1];
				int maxPairedResult = 0;
				for (int t = i; t < j - 4; t++) {
					boolean isPair = pairCheck(seq[t],seq[j]);
					int left,right,pairedResult = 0;
					if (isPair) {
						left = OPTmatrix[i][t-1];
						right = OPTmatrix[t+1][j-1];
						pairedResult = 1 + left + right;
					}
					maxPairedResult = Math.max(maxPairedResult, pairedResult);
				}
				OPTmatrix[i][j] = Math.max(unpairedResult, maxPairedResult);
			}
		}

		return OPTmatrix;
	}
	
	// the traceback algorithm reverse the Nussinov algorithm and output one
	// of the optimal solution of the in the form of parenthesis and dots
	public static void traceback(int i,int j,int[][] OPTmatrix,char[] seq,char[] pairedSeq) {	
		if (j < i) {
			return;
		} else if (OPTmatrix[i][j] == OPTmatrix[i][j-1]) {
			pairedSeq[j] = '.';
			traceback(i,j-1,OPTmatrix,seq,pairedSeq);
			return;
		} else {
			for (int t = i; t < j - 4; t++) {
				if (pairCheck(seq[t],seq[j])) {
					if (OPTmatrix[i][j] == OPTmatrix[i][t-1]+OPTmatrix[t+1][j-1] + 1) {
						pairedSeq[t] = '(';
						pairedSeq[j] = ')';
						//System.out.printf("%c%d is paired with %c%d \n",seq[t],t,seq[j],j);
						traceback(i,t-1,OPTmatrix,seq,pairedSeq);
						traceback(t+1,j-1,OPTmatrix,seq,pairedSeq);
						return;
					}
				}
			}
		}		
	}
	
	// This algorithm checks whether the two input characters can be a base pair
	public static boolean pairCheck(char a, char b) {
		if (a + b == 'A' + 'U' || a + b == 'C' + 'G') return true;
		return false;
	}
	
	// print the OPT table is the input length is not longer than 25
	public static void printMatrix(int[][] OPTmatrix) {
		if (OPTmatrix.length - 1 > 25 ) return;
		for (int i = 1; i < OPTmatrix.length; i++) {
			for (int j = 1; j < OPTmatrix.length; j++) {
				System.out.print(OPTmatrix[i][j] + " ");
				if (OPTmatrix[i][j] < 10) {
					System.out.print(" ");
				}
			}
			System.out.println();
		}		
	}	
	
	// This method generates a random string sequence that is equally likely
	// to have character A,G,U,C at each position
	public static char[] randomSeq(int k) {
		Random r = new Random();
		int length = (int)Math.pow(2, k) + 1;
		char[] output = new char[length];
		char[] rnaBase = {'A','G','U','C'};
		for (int i = 1; i < length; i++) {
			output[i] = rnaBase[r.nextInt(4)];
		}
		return output;
	}
}
