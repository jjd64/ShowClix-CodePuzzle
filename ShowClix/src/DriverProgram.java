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
	private static final String RESERVATION_ERROR = "ERROR - Invalid reservation in input : ";
	private static final String RESERVATION_WARNING = "WARNING - Seat cannot be reserved : ";
	private static final String GROUP_ERROR = "ERROR - Invalid group size in input : ";

	public static void main(String[] args) {
		
		SeatingChart seatingChart = new SeatingChart(ROWS, COLUMNS);
		Scanner scanner = new Scanner(System.in);
		
		// Parse list of reserved seats from stdin
		String reservedString = scanner.nextLine();
		String[] reservations = reservedString.split(" +");
		
		for (String seatStr : reservations) {
			
			String[] splitOnR = seatStr.split("R");
			
			int row;
			int column;
			
			try {
				
				String[] seatNumbers = splitOnR[1].split("C");
				
				if (splitOnR[0].length() != 0 || seatNumbers.length != 2) {
					throw new IllegalArgumentException();
				}
				
				row = Integer.parseInt(seatNumbers[0]);
				column = Integer.parseInt(seatNumbers[1]);
				
			} catch (IndexOutOfBoundsException | IllegalArgumentException | InputMismatchException ex) {
				
				System.out.println(RESERVATION_ERROR + seatStr);
				continue;
			}
			
			if (!seatingChart.reserveSeat(row, column)) {
				System.out.println(RESERVATION_WARNING + seatStr);
			}
			
		}
		
		
		// Parse subsequent lines of input representing numbers of consecutive seats to reserve
		while (scanner.hasNextLine()) {
			
			int groupSize;
			String groupSizeStr = scanner.nextLine();
			
			try {
				
				groupSize = Integer.parseInt(groupSizeStr);
				
				if (groupSize <=0 ) {
					throw new IllegalArgumentException();
				}
				
			} catch (IllegalArgumentException | InputMismatchException ex) {
				
				System.out.println(GROUP_ERROR + groupSizeStr);
				continue;
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
