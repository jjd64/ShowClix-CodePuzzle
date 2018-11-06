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
	
	/**
	 * Reserves a single seat.
	 * @param row - the row number of the seat (indexed from 1)
	 * @param column - the column number of the seat (indexed from 1)
	 * @return true if the seat was successfully reserved, false if it has already been
	 * 			reserved previously or if the seat number doesn't exist
	 */
	public boolean reserveSeat(int row, int column) {
		
		boolean outOfBounds = row < 1 || row >= reserved.length || column < 1 || column >= reserved[0].length;
				
		if (outOfBounds || isReserved(row, column)) {
			return false;
		} else {
			reserved[row][column] = true;
			seatsAvailable--;
			return true;
		}
	}
	
	/**
	 * Gets the current number of available seats.
	 * @return number of seats still available
	 */
	public int numberOfAvailableSeats() {
		return seatsAvailable;
	}
	
	/**
	 * Checks if a seat has been reserved.
	 * @param row - the row number of the seat (indexed from 1)
	 * @param column - the column number of the seat (indexed from 1)
	 * @return true if the seat is already reserved, false otherwise.
	 */
	public boolean isReserved(int row, int column) {
		return reserved[row][column];
	}
	
	/**
	 * Returns the best choice of contiguous seats.
	 * The "best" choice is determined by the smallest Manhattan Distance from the middle
	 * of the front row.
	 * @param numRequested - number of desired consecutive seats
	 * @return a ConsecutiveSeats data structure representing the consecutive seats, or NULL
	 * 			if no such group of seats exists
	 */
	public ConsecutiveSeats getConsecutiveSeats(int numRequested) {
		
		int rowSize = reserved.length - 1;
		int columnSize = reserved[0].length - 1;
		
		if (numRequested > columnSize || numRequested < 1) {
			return null;
		}
		
		ConsecutiveSeats bestSeats = null;
		double middleColumn = (columnSize + 1) / 2.0;
		double smallestDistance = Double.MAX_VALUE;						// Manhattan distance of closest group found
		int minAchievableDistance = 2 * gaussSum((numRequested-1)/2);	// Smallest conceivable Manhattan distance for group of this size
		
		for (int row = 1; row <= rowSize; row++) {
			
			int numAvailable = 0;			// Number of contiguous available seats found
			double aggregateDistance = 0;	// Aggregate Manhattan distance of contiguous available seats
			
			for (int column = 1; column <= columnSize; column++) {
				
				if (!isReserved(row, column)) {
					
					// Add Manhattan distance of available seat moving into the window
					numAvailable++;
					aggregateDistance += manhattanDistance(row, column, middleColumn);
					
				} else {
					
					// Move window past the reserved seat
					numAvailable = 0;
					aggregateDistance = 0;
					continue;
					
				}
				
				if (numAvailable > numRequested) {
					
					// Subtract Manhattan distance of seat moving out of the window
					numAvailable--;
					aggregateDistance -= manhattanDistance(row, column - numRequested, middleColumn);
					
				}
				
				if (numAvailable == numRequested && aggregateDistance < smallestDistance) {
					
					// Found better seating option
					smallestDistance = aggregateDistance;
					bestSeats = new ConsecutiveSeats(row, column + 1 - numRequested, column);
					
				}
					
			}
			
			int nextMinAchievableDistance = (row) + minAchievableDistance;	// Minimum conceivable Manhattan distance in the next row
			if (smallestDistance < nextMinAchievableDistance) {
				// No better group can be found at a higher row
				break;
			}
			
			numAvailable = 0;
			aggregateDistance = 0;
			
		}
		
		return bestSeats;
		
	}
	
	/**
	 * Calculates the Manhattan distance of a seat from the center column of the front row
	 * @param row - row number of seat
	 * @param column - column number of seat
	 * @param middleColumn - the middle column of the seating chart
	 * @return Manhattan distance of seat from the center column of the front row
	 */
	private static double manhattanDistance(int row, int column, double middleColumn) {
		return (row - 1) + Math.abs(column - middleColumn);
	}
	
	/**
	 * Calculates the sum of the integers from 0 to n
	 * @param n - integer to sum to
	 * @return sum of integers from 0 to n (inclusive)
	 * @throws IllegalArgumentException if n is less than 0
	 */
	private static int gaussSum(int n) {
		
		if (n < 0) {
			throw new IllegalArgumentException("n must be greater than 0");
		}
		
		return n * (n+1) / 2;
	}
	
	/**
	 * Reserves a contiguous group of seats in the same row
	 * @param seatGroup - a group of consecutive seats to be reserved
	 * @return true if the reservation was successful, false if the group contained a previously
	 * 			reserved seat
	 */
	public boolean reserveGroup(ConsecutiveSeats seatGroup) {
		
		// check that none of the seats in the group are already reserved
		for (int column = seatGroup.firstColumn; column <= seatGroup.lastColumn; column++) {
			if (isReserved(seatGroup.row, column)) {
				return false;
			}
		}
		
		// reserve each seat in the group
		for (int column = seatGroup.firstColumn; column <= seatGroup.lastColumn; column++) {
			reserveSeat(seatGroup.row, column);
		}
		
		return true;
		
	}
	
	/**
	 * @return tabular format of seating chart : X-reserved, O-available
	 */
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Row #\n");
		
		for (int currentRow = 1; currentRow < reserved.length; currentRow++){
			
			sb.append(currentRow + "\t");
			boolean[] row = reserved[currentRow];
			
			for (int currentColumn = 1; currentColumn < row.length; currentColumn++) {
				
				boolean reservedSeat = row[currentColumn];
				
				if (reservedSeat) {
					sb.append("X");
				} else {
					sb.append("O");
				}
			}
			
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * Data structure representing a consecutive group of seats in a single row
	 */
	class ConsecutiveSeats {
		private int row;
		private int firstColumn;
		private int lastColumn;
		
		/**
		 * Constructor
		 * @param row - the row number of the consecutive seats
		 * @param firstColumn - the column number of the first seat
		 * @param lastColumn - the column number of the last seat
		 */
		public ConsecutiveSeats(int row, int firstColumn, int lastColumn) {
			this.row = row;
			this.firstColumn = firstColumn;
			this.lastColumn = lastColumn;
		}
		
		@Override
		public String toString() {
			
			String prefix = "R" + row + "C";
			StringBuilder sb = new StringBuilder(prefix);
			sb.append(firstColumn);
			
			if (firstColumn != lastColumn) {
				sb.append(" - " + prefix + lastColumn);
			}
			
			return sb.toString();
		}
		
	}
	
}
