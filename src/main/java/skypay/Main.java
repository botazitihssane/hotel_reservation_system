package skypay;

import skypay.model.RoomType;
import skypay.service.Service;

import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Service bookingService = new Service();

        try {
            bookingService.setRoom(1, RoomType.STANDARD, 1000);
            bookingService.setRoom(2, RoomType.JUNIOR, 2000);
            bookingService.setRoom(3, RoomType.SUITE, 3000);

            bookingService.setUser(1, 5000);
            bookingService.setUser(2, 10000);

            Calendar cal = Calendar.getInstance();

            cal.set(2026, Calendar.JUNE, 30);
            Date checkIn1 = cal.getTime();
            cal.set(2026, Calendar.JULY, 7);
            Date checkOut1 = cal.getTime();
            bookingService.bookRoom(1, 2, checkIn1, checkOut1);

            cal.set(2026, Calendar.JULY, 7);
            Date invalidCheckIn = cal.getTime();
            cal.set(2026, Calendar.JUNE, 30);
            Date invalidCheckOut = cal.getTime();
            bookingService.bookRoom(1, 2, invalidCheckIn, invalidCheckOut);

            cal.set(2026, Calendar.JULY, 7);
            Date checkIn2 = cal.getTime();
            cal.set(2026, Calendar.JULY, 8);
            Date checkOut2 = cal.getTime();
            bookingService.bookRoom(1, 1, checkIn2, checkOut2);

            cal.set(2026, Calendar.JULY, 7);
            Date checkIn3 = cal.getTime();
            cal.set(2026, Calendar.JULY, 9);
            Date checkOut3 = cal.getTime();
            bookingService.bookRoom(2, 1, checkIn3, checkOut3);

            cal.set(2026, Calendar.JULY, 7);
            Date checkIn4 = cal.getTime();
            cal.set(2026, Calendar.JULY, 8);
            Date checkOut4 = cal.getTime();
            bookingService.bookRoom(2, 3, checkIn4, checkOut4);

            bookingService.setRoom(1, RoomType.SUITE, 10000);

            System.out.println("\n" + "=".repeat(50));
            System.out.println("FINAL RESULTS");
            System.out.println("=".repeat(50));

            bookingService.printAll();

            System.out.println("\n" + "=".repeat(50));

            bookingService.printAllUsers();

        } catch (Exception e) {
            System.out.println("Error in test execution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}