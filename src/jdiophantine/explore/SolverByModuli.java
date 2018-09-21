package jdiophantine.explore;

import java.math.BigInteger;

/**
 * SolverByModuli tests modulo i (i=2,...) the Diophantine equation:<br /><br />
 * &nbsp;&nbsp;&nbsp;Ax^n + B = Cy^m + D<br />
 * &nbsp;&nbsp;&nbsp;with A,B,C,D,n,m all integers and A,C,n,m > 0<br /><br />
 * This is only a necessary condition, before trying to solve the equation by
 * some other algorithm or class (such as SolverByTrials).<br />
 * For small moduli the check is very fast: if it fails, the equation cannot
 * have any solution.
 * 
 * @author dtagliabue
 *
 */
public class SolverByModuli extends Solver {
	private int maxModulo = 561;
	boolean onlyPrimeModuli = false;

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
	public SolverByModuli(int lMult, int lPow, int lAdd, int rMult, int rPow, int rAdd)
		throws IllegalArgumentException {

		super(lMult, lPow, lAdd, rMult, rPow, rAdd);
	}
	
	/**
	 * Set the highest modulo to be checked
	 * 
	 * @param maxModulo
	 */
	public void setMaxModulo(int maxModulo) {
		this.maxModulo = maxModulo;
	}

	/**
	 * Set the onlyPrimeModuli flag
	 * 
	 * @param onlyPrimeModuli true to test only prime numbers
	 */
	public void setOnlyPrimeModuli(boolean onlyPrimeModuli) {
		this.onlyPrimeModuli = onlyPrimeModuli;
	}
	
	/**
	 * Check if the given equation can have some solution mod n,
	 * with n = 2,... up to a max (e.g. 200). All numbers or just
	 * prime numbers are tested, according to the value of the flag
	 * onlyPrimeModuli (default: all numbers).<br />
	 * This is only a necessary condition: even if the equation can
	 * be solved for all tested moduli, this does not mean that a
	 * solution really exists.
	 * 
	 * @return true if the equation can be solved for all moduli
	 */
	public boolean solve( ) {
		startElaboration = System.currentTimeMillis( );
		boolean possible = true;

		for (int i=2; i<maxModulo; i++) {
			possible = analyzeModulo(i);
			
			if (!possible) break;
		}

		endElaboration = System.currentTimeMillis( );

		return possible;
	}

	/**
	 * Given a modulo i, test for all values for x and y between 0 and i-1
	 * if the two sides of the equation (Ax^n+B and Cy^m+D) have some common
	 * values mod i (e.g.: 2x+1 = 2y cannot be solved mod 2).<br />
	 * Note that this is only a necessary condition: a solution might not
	 * exists even if this check is passed for all moduli.
	 *  
	 * @param i the modulo i to be tested
	 * @return true if the given equation can have solutions mod i
	 */
	public boolean analyzeModulo(int i) {
		BigInteger bigModulo = BigInteger.valueOf(i);
		if (!bigModulo.isProbablePrime(1) && onlyPrimeModuli) return true;
		
		boolean[] possibleY = new boolean[i];
		boolean[] possibleX = new boolean[i];
	
		for (int y=0; y<i; y++) {
			int m = rightExpression(BigInteger.valueOf(y)).mod(BigInteger.valueOf(i)).intValue( );
			possibleY[m] = true;
		}
		
		for (int x=0; x<i; x++) {
			int m = leftExpression(BigInteger.valueOf(x)).mod(BigInteger.valueOf(i)).intValue( );
			possibleX[m] = true;
		}
	
		int possible = 0;
		
		for (int c=0; c<i; c++) {
			if (possibleX[c] && possibleY[c]) {
				possible++;
			}
		}
		
		if (possible == 0 && verbose) {
			System.out.println("  There are no solutions mod " + i);
		}
		
		return (possible > 0);
	}
	
	/**
	 * Main module (to test the class)
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		try {
			SolverByModuli s = new SolverByModuli(6,2,32, 1,3,0);
			s.setMaxModulo(100);
			s.printEquation( );
			s.solve( );
			s.printElapsed( );
		} catch (IllegalArgumentException e) {
			e.printStackTrace( );
		}
	}
}
