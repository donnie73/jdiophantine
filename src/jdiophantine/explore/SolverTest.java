package jdiophantine.explore;

/**
 * 
 * @author dtagliabue
 *
 */
public class SolverTest {
	public static void main(String[] args) {
		try {
			for (int b=1; b<=100; b++) {
				SolverByModuli sm = new SolverByModuli(6,2,b, 1,3,0);
				sm.setMaxModulo(250);
				sm.onlyPrimeModuli = false;
				sm.printEquation( );
				if (!sm.checkGCD( )) continue;
				if (!sm.solve( )) continue;
					
				SolverByTrials st = new SolverByTrials(6,2,b, 1,3,0);
				st.setLimit(1000000);
				st.setStopAtFirst(false);
				st.solve( );
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace( );
		}
	}
}
