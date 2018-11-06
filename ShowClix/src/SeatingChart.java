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
	 * @param numOfSeats - number of desired consecutive seats
	 * @return a ConsecutiveSeats data structure representing the consecutive seats, or NULL
	 * 			if no such group of seats exists
	 */
	public ConsecutiveSeats getConsecutiveSeats(int numOfSeats) {
		
		if (numOfSeats >= reserved[0].length) {
			return null;
		}
		
		ConsecutiveSeats bestSeats = null;
		int smallestDist = Integer.MAX_VALUE;
		
		// TODO - get "best" group of contiguous seats based on smallest Manhattan distance
		
		return bestSeats;
		
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
	 * Data structure representing a consecutive group of seats in the same row
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
