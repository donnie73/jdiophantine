package jdiophantine.explore;

import java.math.BigInteger;

/**
 * SolverByTrials tries to find the integer solutions to the Diophantine equation:<br /><br />
 * &nbsp;&nbsp;&nbsp;Ax^n + B = Cy^m + D<br />
 * &nbsp;&nbsp;&nbsp;with A,B,C,D,n,m all integers and A,C,n,m > 0<br /><br />
 * e.g.: 6x^2 + 2 = y^3 (which has no solutions)<br />
 * All possible integer values for y and x are tested, starting from 2,
 * up to a maximum specified for y.<br /><br />
 * To improve the performances, before starting the main loop with the trials,
 * the equation is analyzed mod 3,5,7,8,11,13,17 (by default) in order to skip
 * y values that cannot have any congruent solution (e.g. in 6x^2+2=y^3, y must be odd).<br />
 * Performances: with a I7 core, about 3000ms to test first 10 million y values
 * 
 * @author Donato Tagliabue
 *
 */
public class SolverByTrials extends Solver {
	private int[] moduliToTest = {840, 1104, 2431}; //840=3*5*7*8, 2431=11*13*17
	private BigInteger[] bigModuli;
	private boolean[][] possibleModuli;
	
	private long rLimit = 1000000;
	private boolean stopAtFirstSolution = false;
	
	private BigInteger lastMin;
	private BigInteger yTrials, totalTrials;
	
	/**
	 * Constructor to set the equation Ax^n + B = Cy^m + D to be tested
	 * 
	 * @param lMult The value for A>0
	 * @param lPow The value for n>0
	 * @param lAdd The value for B
	 * @param rMult The value for C>0
	 * @param rPow The value for m>0
	 * @param rAdd The value for D
	 * @throws IllegalArgumentException
	 */
	public SolverByTrials(int lMult, int lPow, int lAdd, int rMult, int rPow, int rAdd)
		throws IllegalArgumentException {

		super(lMult, lPow, lAdd, rMult, rPow, rAdd);
		
		yTrials = BigInteger.ZERO;
		totalTrials = BigInteger.ZERO;
	}
	
	/**
	 * Set the upper limit for y<br />
	 * Default: 1000000
	 * 
	 * @param rLimit
	 */
	public void setLimit(long rLimit) throws IllegalArgumentException {
		if (rLimit < 2) throw new IllegalArgumentException("Invalid limit (min is 2)");
		
		this.rLimit = rLimit;
	}
	
	/**
	 *  Set stopAtFirst flag (if true, stop finding other solutions when the first is found).<br />
	 *  Default: false
	 *  
	 * @param stopAtFirst
	 */
	public void setStopAtFirst(boolean stopAtFirst) {
		this.stopAtFirstSolution = stopAtFirst;
	}
	
	/**
	 * Overwrite the default list of moduli (840, 1104, 2431) used to "solve" the
	 * equation before searching all possible x values (given an y).
	 * 
	 * @param moduliToTest the array of moduli, in any order
	 */
	public void setModuliToTest(int[] moduliToTest) {
		this.moduliToTest = moduliToTest;
	}
	
	/**
	 * "Solve" the given equation by trials. All y's up to a limit are tried, and
	 * for each y an x is searched. If expr(y) can never be congruent to expr(x)
	 * (for a list of moduli), the corresponding y is skipped
	 * 
	 * @return true if at least a solution is found
	 */
	public boolean solve( ) {
		startElaboration = System.currentTimeMillis( );
		boolean solutionFound = false;
		if (possibleModuli == null) findWrongModuli( );
		
		BigInteger r = BigInteger.ONE;
		BigInteger rMax = BigInteger.valueOf(rLimit);
		lastMin = null;
		
		while (r.compareTo(rMax) < 0) {
			r = r.add(BigInteger.ONE);
			BigInteger rExpr = rightExpression(r);
			if (hasWrongModuli(rExpr)) continue;
			
			boolean found = checkThisRightExpression(r, rExpr);
			if (found) solutionFound = true;
			if (found && stopAtFirstSolution) break;
		}
		
		if (!solutionFound) {
			if (verbose) System.out.println("  No solution up to y=" + rLimit);
		}
		endElaboration = System.currentTimeMillis( );
		
		return solutionFound;
	}
	
	/**
	 * Print the number of trials (for y and x, after skipping the "wrong" moduli)
	 */
	public void printTrials( ) {
		System.out.println("  Trials: " + totalTrials + " x values for " + yTrials + " y values");
	}
	
	/**
	 * Print the list of moduli and, for each modulus, the number of values that can have the left
	 * expression (when x is between 0 and mod-1)
	 */
	public void printWrongModuli( ) {
		for (int y=0; y<moduliToTest.length; y++) {
			int wrong = 0;
			
			for (int x=0; x<moduliToTest[y]; x++) {
				if (!possibleModuli[y][x]) wrong++;
			}
			
			System.out.println("  Left expression has " + (moduliToTest[y] - wrong) + " values modulo " + moduliToTest[y]);
		}
	}
	
	/**
	 * Given a list of moduli (e.g. 3,5,7,8), verify which values the left expression
	 * of the equation (the "x part") can assume.<br />E.g: if modulo is 3, and we have
	 * expr(x=0)=2 (mod 3), expr(x=1)=1 (mod 3), expr(x=2)=2 (mod 3), then expr(x)
	 * can never be congruent to 0. Store this information in a bidimensional array
	 * array[0][0]=false, array[0][1]=true, array[0][2]=true<br />
	 * (the first index is the index of the modulo in the list)
	 */
	public void findWrongModuli( ) {
		int numberOfModuli = moduliToTest.length, highestModulo = 0;
		
		for (int m : moduliToTest) {
			if (m > highestModulo) {
				highestModulo = m;
			}
		}

		possibleModuli = new boolean[numberOfModuli][highestModulo];
		bigModuli = new BigInteger[numberOfModuli];
		
		for (int i=0; i<numberOfModuli; i++) {
			bigModuli[i] = BigInteger.valueOf(moduliToTest[i]);
			
			for (int x=0; x<moduliToTest[i]; x++) {
				int m = leftExpression(BigInteger.valueOf(x)).mod(bigModuli[i]).intValue( );
				possibleModuli[i][m] = true;
			}
		}
	}
	
	/**
	 * Given an expression for y, check if mod k the left part can have a solution,
	 * for a list of k. E.g: if expr(y) mod 3 = 1, but the left part is never congruent
	 * to 1 mod 3 for any x, do not start searching a solution for x and move to the next y.
	 * 
	 * @param n the y expression (not y)
	 * @return true if the left part cannot be congruent to the right part for at least one modulo
	 */
	private boolean hasWrongModuli(BigInteger n) {
		for (int y=0; y<moduliToTest.length; y++) {
			int m = n.mod(bigModuli[y]).intValue( );
			if (!possibleModuli[y][m]) return true;
		}

		return false;
	}
	
	/**
	 * Given an y, try to find an x value which satisfies the equation.<br />
	 * The algorithm is the following:<br />
	 * - start with x=2, min and max unassigned<br />
	 * - calculate the x expression and compare to the y expression<br />
	 * - if expr(x)=expr(y), got it! found a solution<br />
	 * - if expr(x)<expr(y), set min=x and x=(min+max)/2<br />
	 *   (if max is unassigned, set x=x*2)<br />
	 * - if expr(x)>expr(y), set max=x and x=(min+max)/2<br />
	 * In this way, the range [min,max] narrows down: stop when it's 1
	 * 
	 * @param y The y value
	 * @param rExpr The value of the right part of the equation
	 * @return true if a solution is found
	 */
	private boolean checkThisRightExpression(BigInteger y, BigInteger rExpr) {
		boolean xFound = false;
		BigInteger x = (lastMin == null) ? TWO : lastMin;
		BigInteger min = lastMin, max = null;
		yTrials = yTrials.add(BigInteger.ONE);
		
		while (!xFound) {
			BigInteger lExpr = leftExpression(x);
			int compareExpr = lExpr.compareTo(rExpr);
			totalTrials = totalTrials.add(BigInteger.ONE);

			if (compareExpr == 0) {
				xFound = true;
				solutions.put(x, y);
				if (verbose) System.out.println("  Solution: x=" + x + ", y=" + y);
			}
			else {
				if (compareExpr < 0) {
					min = x;
					
					if (max == null) {
						x = x.multiply(TWO);
					}
					else {
						x = min.add(max).divide(TWO);
					}
				}
				else {
					if (min == null) break;
					
					max = x;
					x = min.add(max).divide(TWO);
				}
				
				if (max != null && max.subtract(min).compareTo(BigInteger.ONE) == 0) break;
			}
		}
		lastMin = min;
		
		return xFound;
	}

	/**
	 * Main module (to test the class)
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		try {
			SolverByTrials s = new SolverByTrials(6,2,2, 2,3,0);
			s.setLimit(10000000);
			s.setStopAtFirst(false);
			s.printEquation( );
			//s.printWrongModuli( );
			s.solve( );
			//s.printTrials( );
			s.printElapsed( );
		} catch (IllegalArgumentException e) {
			e.printStackTrace( );
		}
	}
}