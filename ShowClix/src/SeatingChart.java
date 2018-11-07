package src;

/**
 * Data structure which represents a seating chart.
 * 
 * @author Jonathan DuMont
 *
 */
public class SeatingChart {
	
	private Seat[][] seats;			// All seats in seating chart - first dimension is rows, second is columns
	private Seat[] availableSeats;	// First available seat in each row
	private int numberAvailable;	// Number of remaining available seats
	
	/**
	 * Constructor
	 * @param numRows - number of rows of seats
	 * @param numColumns - number of columns of seats
	 * @throws IllegalArgumentException if the seating chart is initialized with 0 or fewer seats
	 */
	public SeatingChart(int numRows, int numColumns) {
		
		numberAvailable = numRows * numColumns;
		
		if (numberAvailable < 1) {
			throw new IllegalArgumentException("Number of rows and columns must be greater than 0");
		}
		
		seats = new Seat[numRows+1][numColumns+1];
		availableSeats = new Seat[numRows+1];
		
		for (int row = 1; row <= numRows; row++) {
			
			for (int column = 1; column <= numColumns; column++) {
				
				Seat previousSeat = seats[row][column - 1];
				double distance = manhattanDistance(row, column);
				Seat newSeat = new Seat(row, column, distance, previousSeat);
				seats[row][column] = newSeat;
				
				if (previousSeat != null) {
					previousSeat.next = newSeat;
				} else {
					availableSeats[row]= newSeat;
				}
				
			}
			
		}
		
	}
	
	/**
	 * Gets the number of rows in the seating chart
	 * @return number of rows
	 */
	public int numberOfRows() {
		return seats.length - 1;
	}
	
	/**
	 * Gets the number of columns in the seating chart
	 * @return number of columns
	 */
	public int numberOfColumns() {
		return seats[0].length - 1;
	}
	
	/**
	 * Whether the seat number exists in seating chart
	 * @param row - row number
	 * @param column - column number
	 * @return true if seat number exists in seating chart, false otherwise
	 */
	public boolean seatNumberExists(int row, int column) {
		
		boolean validRow = row > 0 && row <= numberOfRows();
		boolean validColumn = column > 0 && column <= numberOfColumns();
		return validRow && validColumn;
		
	}
	
	/**
	 * Reserves a single seat.
	 * @param row - the row number of the seat (indexed from 1)
	 * @param column - the column number of the seat (indexed from 1)
	 * @return true if the seat was successfully reserved, false if it has already been
	 * 			reserved previously or if the seat number doesn't exist
	 */
	public boolean reserveSeat(int row, int column) {
				
		if (seatNumberExists(row, column)) {
			
			Seat seat = seats[row][column];
			
			if (seat.reserve()) {
				numberAvailable--;
				return true;
			}
			
		}
		
		return false;
		
	}
	
	/**
	 * Gets the current number of available seats.
	 * @return number of seats still available
	 */
	public int numberOfAvailableSeats() {
		return numberAvailable;
	}
	
	/**
	 * Checks if a seat has been reserved.
	 * @param row - the row number of the seat (indexed from 1)
	 * @param column - the column number of the seat (indexed from 1)
	 * @return true if the seat is already reserved, false otherwise.
	 * @throws IllegalArgumentException if seat number does not exist in seating chart
	 */
	public boolean isReserved(int row, int column) {
		
		if (!seatNumberExists(row, column)) {
			throw new IllegalArgumentException("Seat number does not exist : R" + row + "C" + column);
		}
		
		Seat seat = seats[row][column];
		return seat.isReserved;
	}
	
	/**
	 * Returns the best choice of contiguous seats based on Manhattan distance
	 * @param numRequested - number of desired consecutive seats
	 * @return a ConsecutiveSeats data structure representing the consecutive seats, or NULL
	 * 			if no such group of seats exists
	 */
	public ConsecutiveSeats getConsecutiveSeats(int numRequested) {
		
		if (numRequested > numberOfColumns() || numRequested < 1) {
			return null;
		}
		
		ConsecutiveSeats bestSeats = null;
		double smallestDistance = Double.MAX_VALUE;						// Manhattan distance of closest group found
		int minAchievableDistance = 2 * gaussSum((numRequested-1)/2);	// Smallest conceivable Manhattan distance for group of this size
		
		for (int row = 1; row <= numberOfRows(); row++) {
			
			int numAvailable = 0;			// Number of contiguous available seats found
			double aggregateDistance = 0;	// Aggregate Manhattan distance of contiguous available seats
			
			// TODO - Consider starting from the middle column and work out in one direction, then the other, continuing
			//			to the next row once we know the aggregate distance would be larger than the smallestDistance
			//			found, or a group is already found (per each direction) - no need to go further out from the middle
			
			Seat nextAvailable = availableSeats[row];
			Seat previousAvailable = null;
			
			while (nextAvailable != null) {
				
				int column = nextAvailable.column;
				
				if (previousAvailable != null && previousAvailable.column != column - 1) {
					
					// Reserved seat was between the two current available seats - reset window
					numAvailable = 0;
					aggregateDistance = 0;
				}
				
				// Add Manhattan distance of available seat moving into the window
				numAvailable++;
				aggregateDistance += nextAvailable.distance;
				
				if (numAvailable > numRequested) {
					
					// Subtract Manhattan distance of seat moving out of the window
					Seat toRemove = seats[row][column - numRequested];
					numAvailable--;
					aggregateDistance -= toRemove.distance;
					
				}
				
				if (numAvailable == numRequested && aggregateDistance < smallestDistance) {
					
					// Found better seating option
					smallestDistance = aggregateDistance;
					bestSeats = new ConsecutiveSeats(row, column + 1 - numRequested, column);
					
				}
				
				previousAvailable = nextAvailable;
				nextAvailable = nextAvailable.next;
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
	 * @return Manhattan distance of seat from the center column of the front row
	 */
	private double manhattanDistance(int row, int column) {
		
		double middleColumn = (numberOfColumns() + 1) / 2.0;
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
		
		for (int row = 1; row <= numberOfRows(); row++){
			
			sb.append(row + "\t");
			
			for (int column = 1; column <= numberOfColumns(); column++) {
				
				boolean reservedSeat = isReserved(row, column);
				
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
	 * Data structure representing a single seat object
	 */
	private class Seat {
		
		private boolean isReserved;	// Whether this seat has been reserved
		private int row;			// Row number of this seat
		private int column;			// Column number of this seat
		private double distance;	// Manhattan distance from center of front row
		private Seat next;			// Next available seat in the same row (null if none exist)
		private Seat previous;		// Previous available seat in the same row (null if none exist)
		
		/**
		 * Constructor
		 * @param row - row number
		 * @param column - column number
		 * @param previous - previous available seat in same row
		 */
		public Seat(int row, int column, double distance, Seat previous) {
			this.row = row;
			this.column = column;
			this.distance = distance;
			this.previous = previous;
		}
		
		/**
		 * Reserves this seat
		 * @return true if seat was successfully reserved, false otherwise
		 */
		private boolean reserve() {
			
			if (this.isReserved) {
				return false;
			} 
			
			this.isReserved = true;
				
			if (next != null) {
				next.previous = this.previous;
			}
				
			if (previous != null) {
				previous.next = this.next;
			} else {
				availableSeats[this.row] = next;
			}
				
			return true;
			
		}
		
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
		
		/**
		 * String representation of a group of consecutive seats
		 * @returns a representation of the seat group in the form of "RxCy - RxCz"
		 * 			where 'x' is the row number, 'y' is the column of the first seat,
		 * 			and 'z' is the column of the last seat in the group.
		 */
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
