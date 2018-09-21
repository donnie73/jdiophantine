package jdiophantine.explore;

import java.math.BigInteger;
import java.util.TreeMap;

/**
 * Base abstract class to deal with Diophantine equations of the form:<br />
 * &nbsp;&nbsp;&nbsp;Ax^n + B = Cy^m + D<br />
 * &nbsp;&nbsp;&nbsp;with A,B,C,D,n,m all integers and A,C,n,m > 0<br /><br />
 * n and m are expected relatively small (they are stored as int values),
 * while A,B,C,D have no limit (they are stored as BigInteger).<br />
 * Only some common helping methods are implemented here (print the
 * equations, compute the left and right part of the equation given x or y,
 * etc.). It's up to a subclass to implement the "solve" method and any
 * other needed method.
 *
 * @author dtagliabue
 *
 */
public abstract class Solver {
	public static final BigInteger TWO = BigInteger.ONE.add(BigInteger.ONE);

	protected int lPow, rPow;
	protected BigInteger lMult, lAdd, rMult, rAdd;

	protected long startElaboration, endElaboration;
	protected boolean verbose = true;

	protected TreeMap<BigInteger, BigInteger> solutions;

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
	public Solver(int lMult, int lPow, int lAdd, int rMult, int rPow, int rAdd)
		throws IllegalArgumentException {

		if (lPow <=0 || rPow <=0) throw new IllegalArgumentException("Powers (x^n, y^m) must be positive");
		if (lMult <=0 || rMult <=0) throw new IllegalArgumentException("Factors (a*x, c*d) must be positive");

		this.lPow  = lPow;
		this.rPow  = rPow;
		this.lMult = BigInteger.valueOf(lMult);
		this.lAdd  = BigInteger.valueOf(Math.abs(lAdd));
		this.rMult = BigInteger.valueOf(rMult);
		this.rAdd  = BigInteger.valueOf(Math.abs(rAdd));
		
		if (lAdd < 0) this.lAdd = this.lAdd.negate( );
		if (rAdd < 0) this.rAdd = this.rAdd.negate( );

		solutions = new TreeMap<BigInteger, BigInteger>( );
	}

	/**
	 * Set verbose flag (if false, do not print solutions on standard output).<br />
	 * Default: true
	 * 
	 * @param verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Get the solutions found (if any). Must be called after the "solve" method
	 * 
	 * @return the TreeMap of (x,y) solutions, as BigIntegers
	 */
	public TreeMap<BigInteger, BigInteger> getSolutions( ) {
		return solutions;
	}
	
	/**
	 * Print the equation (e.g. 6x^2 + 3 = 5y^3 - 1) 
	 */
	public void printEquation( ) {
		String lAddSignum = lAdd.signum( ) >= 0 ? "+" : "-";
		String rAddSignum = rAdd.signum( ) >= 0 ? "+" : "-";

		String expr = lMult.compareTo(BigInteger.ONE) == 0 ? "" : lMult.toString( );
		expr += "x";
		expr += (lPow == 1) ? "" : ("^" + lPow);
		expr += lAdd.compareTo(BigInteger.ZERO) == 0 ? "" : (" " + lAddSignum + " " + lAdd.abs( ));
		expr += " = ";
		expr += rMult.compareTo(BigInteger.ONE) == 0 ? "" : rMult.toString( );
		expr += "y";
		expr += (rPow == 1) ? "" : ("^" + rPow);
		expr += rAdd.compareTo(BigInteger.ZERO) == 0 ? "" : (" " + rAddSignum + " " + rAdd.abs( ));
		
		System.out.println("Equation: " + expr);
	}

	/**
	 * Print elapsed time (must be called after the "solve" method)
	 */
	public void printElapsed( ) {
		long elapsed = endElaboration - startElaboration;
		String elapsedMsg = (elapsed > 10000) ? (elapsed/1000 + "s") : (elapsed + "ms");
		
		System.out.println("Elapsed: " + elapsedMsg);
	}

	/**
	 * Compute the left part of the equation (the "x part")<br />
	 * The general form is Ax^n + B
	 * 
	 * @param x The x value
	 * @return
	 */
	public BigInteger leftExpression(BigInteger x) {
		return x.pow(lPow).multiply(lMult).add(lAdd);
	}
	
	/**
	 * Compute the right part of the equation (the "y part")<br />
	 * The general form is Cy^m + D
	 * 
	 * @param y The y value
	 * @return
	 */
	public BigInteger rightExpression(BigInteger y) {
		return y.pow(rPow).multiply(rMult).add(rAdd);
	}
	
	/**
	 * Given Ax + B = Cy + D, check if gcd(A,C)|(B-D)<br />
	 * (otherwise the equation cannot have any solution because of Bezout's identity)
	 * 
	 * @return true if the gcd divides B-D
	 */
	public boolean checkGCD( ) {
		BigInteger gcd = lMult.gcd(rMult);
		BigInteger k = lAdd.subtract(rAdd);
		boolean hasSolution = (k.mod(gcd) == BigInteger.ZERO);

		if (verbose && !hasSolution) {
			System.out.println("  No solutions by Bezout's identity");
		}

		return hasSolution;
	}
	
	/**
	 * Abstract method to be implemented in subclasses, with some algorithm
	 * to solve the Diophantine equation
	 * 
	 * @return true if at least a solution is found
	 */
	public abstract boolean solve( );
}
