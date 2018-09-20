package jdiophantine.explore;

public class SolverByModuli extends Solver {
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

	public boolean solve( ) {
		// class under construction: check left and right expressions for all moduli
		// (2,3,4, etc.) up to a given maximum

		return false;
	}

	/**
	 * Main module (to test the class)
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		try {
			SolverByModuli s = new SolverByModuli(6,2,2, 2,3,0);
			//s.solve( );
			s.printElapsed( );
		} catch (IllegalArgumentException e) {
			e.printStackTrace( );
		}
	}
}
