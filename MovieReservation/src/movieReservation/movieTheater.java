package movieReservation;

import java.util.concurrent.Semaphore;

public class movieTheater {

	private static final int MAX_AVAILABLE = 1;
	private final Semaphore available = new Semaphore(MAX_AVAILABLE, true);
	private String[] seatingChart = null;
	private int capacity;
	
	//Initializes a theater with size number of seats
	public movieTheater(int size) {
		seatingChart = new String[size];
		capacity = size;
		
		for (int i=0; i<capacity; i++)
			seatingChart[i] = null;
	}
	
	public synchronized String reserveSeat(String name) {
		String result = null;
		
		//Check if theater is empty
		if (capacity == 0)
			result = "Sold out - No seat available";
		
		else if (search(name) == -1) {
			capacity = capacity - 1;
			//We will assign the first null element as the seat
			for (int seat=0; seat<seatingChart.length; seat++) {
				if (seatingChart[seat] == null) {
					seatingChart[seat] = name;
					result = "Seat assigned to you is " + Integer.toString(seat);
					break;	
				}				
			}
		}
		else {
			result = "Seat already booked against the name provided";
		}
		
		return result;
	}
	
	public synchronized String bookSeat(String name, int seat) {
		String result = null;
		
		//First check to see if theater is empty
		if (capacity == 0)
			return "Sold out - No seat available";
		
		if (seat >= capacity)
			return (Integer.toString(seat) + " is not available");
		
		//Check to see if the requested seat number is available
		if (seatingChart[seat] != null)
			return (Integer.toString(seat) + " is not available");
		
		//Make sure the person has not already booked a seat
		if (search(name) == -1) {
			capacity = capacity - 1;
			//Reserve the seat to this person's name
			seatingChart[seat] = name;
			result = "Seat assigned to you is " + Integer.toString(seat);
		}
		else {
			result = "Seat already booked against the name provided";
		}
		
		return result;
	}
	
	//Returns the index of the array where name was found
	//Returns -1 if name is not in the array
	public synchronized int search(String name) {
		for (int seat=0; seat<seatingChart.length; seat++) {
			if ((seatingChart[seat] != null) && (seatingChart[seat].equals(name)))
				return seat;				
		}
		
		//If we have exited the loop, that means the name was not found
		return -1;
	}
	
	public synchronized String delete(String name) {
		String result = null;
		int seatNumber = 0;
		
		seatNumber = search(name);
		
		//Any number other than -1 is the seat number to be released
		if (seatNumber != -1) {
			result = "Seat released is " + Integer.toString(seatNumber);
			seatingChart[seatNumber] = null;
			capacity = capacity + 1;
		}
		else
			result = "No reservation found for " + name;
		
		return result;
	}
}
