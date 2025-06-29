package skypay.service;

import skypay.model.Booking;
import skypay.model.Room;
import skypay.model.RoomType;
import skypay.model.User;

import java.util.ArrayList;
import java.util.Date;

public class Service {
    ArrayList<Room> rooms;
    ArrayList<User> users;
    ArrayList<Booking> bookings;

    public Service() {
        rooms = new ArrayList<>();
        users = new ArrayList<>();
        bookings = new ArrayList<>();
    }

    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        try {
            if (roomType == null) {
                throw new IllegalArgumentException("Room type cannot be null");
            }
            if (roomPricePerNight < 0) {
                throw new IllegalArgumentException("Room price cannot be negative");
            }

            Room existingRoom = findRoomByNumber(roomNumber);
            if (existingRoom != null) {
                existingRoom.setRoomType(roomType);
                existingRoom.setRoomPricePerNight(roomPricePerNight);
            } else {
                Room newRoom = new Room(roomNumber, roomType, roomPricePerNight);
                rooms.add(0, newRoom);
            }
        } catch (Exception e) {
            System.out.println("Error setting room: " + e.getMessage());
        }
    }

    public void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        try {
            if (checkIn == null || checkOut == null) {
                throw new IllegalArgumentException("Check-in and check-out dates cannot be null");
            }
            if (checkIn.compareTo(checkOut) >= 0) {
                throw new IllegalArgumentException("Check-in date must be before check-out date");
            }

            User user = findUserById(userId);
            Room room = findRoomByNumber(roomNumber);

            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            if (room == null) {
                throw new IllegalArgumentException("Room not found");
            }

            long diffInMillies = checkOut.getTime() - checkIn.getTime();
            int nights = (int) (diffInMillies / (1000 * 60 * 60 * 24));
            int totalCost = nights * room.getRoomPricePerNight();

            if (user.getBalance() < totalCost) {
                throw new IllegalArgumentException("Insufficient balance for booking");
            }

            if (!isRoomAvailable(roomNumber, checkIn, checkOut)) {
                throw new IllegalArgumentException("Room is not available for the specified period");
            }

            Booking booking = new Booking(userId, roomNumber, checkIn, checkOut, totalCost, nights,
                    room.getRoomType(), room.getRoomPricePerNight(), user.getBalance());
            bookings.add(0, booking);
            user.setBalance(user.getBalance() - totalCost);

            System.out.println("Booking successful: " + booking);

        } catch (Exception e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    public void printAll() {
        System.out.println("=== ALL ROOMS AND BOOKINGS ===");

        System.out.println("\n--- ROOMS ---");
        for (Room room : rooms) {
            System.out.println(room);
        }

        System.out.println("\n--- BOOKINGS ---");
        for (Booking booking : bookings) {
            System.out.println(booking);
        }
    }

    public void setUser(int userId, int balance) {
        try {
            if (balance < 0) {
                throw new IllegalArgumentException("Balance cannot be negative");
            }

            User existingUser = findUserById(userId);
            if (existingUser != null) {
                existingUser.setBalance(balance);
            } else {
                User newUser = new User(userId, balance);
                users.add(0, newUser);
            }
        } catch (Exception e) {
            System.out.println("Error setting user: " + e.getMessage());
        }
    }

    public void printAllUsers() {
        System.out.println("=== ALL USERS ===");
        for (User user : users) {
            System.out.println(user);
        }
    }

    private User findUserById(int userId) {
        for (User user : users) {
            if (user.getUserId() == userId) {
                return user;
            }
        }
        return null;
    }

    private Room findRoomByNumber(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    private boolean isRoomAvailable(int roomNumber, Date checkIn, Date checkOut) {
        for (Booking booking : bookings) {
            if (booking.getRoomNumber() == roomNumber) {
                if (!(checkOut.compareTo(booking.getCheckIn()) <= 0 ||
                        checkIn.compareTo(booking.getCheckOut()) >= 0)) {
                    return false;
                }
            }
        }
        return true;
    }
}