package src;

/**
 * Data structure which represents a seating chart.
 * 
 * @author Jonathan DuMont
 *
 */
public class SeatingChart {
	
	private boolean[][] reserved;
	private int seatsAvailable;
	
	/**
	 * Constructor
	 * @param numRows - number of rows of seats
	 * @param numColumns - number of columns of seats
	 * @throws IllegalArgumentException if the seating chart is initialized with 0 or fewer seats
	 */
	public SeatingChart(int numRows, int numColumns) {
		
		if (numRows < 1) {
			throw new IllegalArgumentException("Number of rows must be greater than 0");
		}
		
		if (numColumns < 1) {
			throw new IllegalArgumentException("Number of columns must be greater than 0");
		}
		
		reserved = new boolean[numRows+1][numColumns+1];
		seatsAvailable = numRows * numColumns;
	}
	
	
	// TODO
	
	
}
