package src;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * A driver program for the SeatingChart class.
 * 
 * The first line of input (Up to a newline character) should be space-separated inputs in the
 * form of "RxCy" where x is the row number and y is the column number. These seats are the
 * initial reservations.
 * 
 * Subsequent lines of input (Up to an EOF character) should be integers representing the
 * number of consecutive seats to reserve.
 * 
 * Upon EOF, outputs the number of remaining available seats
 * 
 * @author Jonathan DuMont
 *
 */
public class DriverProgram {
	
	private static final int ROWS = 3;
	private static final int COLUMNS = 11;
	private static final String ERROR_MESSAGE = "First line of input must be in the form of space-"
			+ "separated values of \"RxCy\" where x and y are each an integer value.";

	public static void main(String[] args) {
		
		SeatingChart seatingChart = new SeatingChart(ROWS, COLUMNS);
		Scanner scanner = new Scanner(System.in);
		
		// Read in list of reserved seats from stdin
		String reservedString = scanner.nextLine();
		String[] reservedArray = reservedString.split(" ");
		
		for (String seatStr : reservedArray) {
			String[] seatNumbers = seatStr.split("R|C");
			
			if (seatNumbers.length != 3) {
				scanner.close();
				throw new IllegalArgumentException(ERROR_MESSAGE);
			}
			
			int row;
			int column;
			
			try {
				
				row = Integer.parseInt(seatNumbers[1]);
				column = Integer.parseInt(seatNumbers[2]);
				
			} catch (InputMismatchException | NumberFormatException ex) {
				
				scanner.close();
				throw new IllegalArgumentException(ERROR_MESSAGE);
			}
			
			seatingChart.reserveSeat(row, column);
			
		}
		
		
		// Read in subsequent lines of input representing numbers of consecutive seats to reserve
		while (scanner.hasNextLine()) {
			
			int groupSize;
			String groupSizeStr = scanner.nextLine();
			
			try {
				
				groupSize = Integer.parseInt(groupSizeStr);
				
			} catch (InputMismatchException | NumberFormatException ex) {
				
				scanner.close();
				throw new IllegalArgumentException("Invalid group size in input file : " + groupSizeStr);
			}
			
			SeatingChart.ConsecutiveSeats seatGroup = seatingChart.getConsecutiveSeats(groupSize);
			
			if (seatGroup != null) {
				
				seatingChart.reserveGroup(seatGroup);
				System.out.println(seatGroup);
				
			} else {
				
				System.out.println("Not Available");
			}
			
		}
		
		System.out.println(seatingChart.numberOfAvailableSeats());
		
		scanner.close();
	}

}
