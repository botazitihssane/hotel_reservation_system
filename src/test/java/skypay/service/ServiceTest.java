package skypay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import skypay.model.RoomType;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceTest {
    private Service service;
    private Date checkIn;
    private Date checkOut;

    @BeforeEach
    void setUp() {
        service = new Service();
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JULY, 1);
        checkIn = cal.getTime();
        cal.set(2026, Calendar.JULY, 3);
        checkOut = cal.getTime();
    }

    @Nested
    @DisplayName("Room Management")
    class RoomManagement {

        @Test
        @DisplayName("Should create new room when room number does not exist")
        void shouldCreateNewRoomWhenRoomNumberDoesNotExist() {
            // Given
            int roomNumber = 101;
            RoomType roomType = RoomType.STANDARD;
            int price = 1000;

            // When
            service.setRoom(roomNumber, roomType, price);

            // Then
            assertEquals(1, service.rooms.size());
            assertEquals(roomNumber, service.rooms.get(0).getRoomNumber());
            assertEquals(roomType, service.rooms.get(0).getRoomType());
            assertEquals(price, service.rooms.get(0).getRoomPricePerNight());
        }

        @Test
        @DisplayName("Should update existing room when room number already exists")
        void shouldUpdateExistingRoomWhenRoomNumberAlreadyExists() {
            // Given
            service.setRoom(101, RoomType.STANDARD, 1000);

            // When
            service.setRoom(101, RoomType.SUITE, 2000);

            // Then
            assertEquals(1, service.rooms.size());
            assertEquals(RoomType.SUITE, service.rooms.get(0).getRoomType());
            assertEquals(2000, service.rooms.get(0).getRoomPricePerNight());
        }

        @Test
        @DisplayName("Should not affect existing bookings when room is updated")
        void shouldNotAffectExistingBookingsWhenRoomIsUpdated() {
            // Given
            service.setRoom(101, RoomType.STANDARD, 1000);
            service.setUser(1, 5000);
            service.bookRoom(1, 101, checkIn, checkOut);

            // When
            service.setRoom(101, RoomType.SUITE, 3000);

            // Then
            assertEquals(1, service.bookings.size());
            assertEquals(RoomType.STANDARD, service.bookings.get(0).getRoomTypeAtBooking());
            assertEquals(1000, service.bookings.get(0).getRoomPriceAtBooking());
            assertEquals(2000, service.bookings.get(0).getTotalCost());
        }
    }

    @Nested
    @DisplayName("User Management")
    class UserManagement {

        @Test
        @DisplayName("Should create new user when user ID does not exist")
        void shouldCreateNewUserWhenUserIdDoesNotExist() {
            // Given
            int userId = 1;
            int balance = 5000;

            // When
            service.setUser(userId, balance);

            // Then
            assertEquals(1, service.users.size());
            assertEquals(userId, service.users.get(0).getUserId());
            assertEquals(balance, service.users.get(0).getBalance());
        }

        @Test
        @DisplayName("Should update existing user balance when user ID already exists")
        void shouldUpdateExistingUserBalanceWhenUserIdAlreadyExists() {
            // Given
            service.setUser(1, 5000);

            // When
            service.setUser(1, 8000);

            // Then
            assertEquals(1, service.users.size());
            assertEquals(8000, service.users.get(0).getBalance());
        }
    }

    @Nested
    @DisplayName("Room Booking - Success Cases")
    class RoomBookingSuccessCases {

        @Test
        @DisplayName("Should create booking and deduct balance when all conditions are met")
        void shouldCreateBookingAndDeductBalanceWhenAllConditionsAreMet() {
            // Given
            service.setRoom(101, RoomType.STANDARD, 1000);
            service.setUser(1, 5000);

            // When
            service.bookRoom(1, 101, checkIn, checkOut);

            // Then
            assertEquals(1, service.bookings.size());
            assertEquals(3000, service.users.get(0).getBalance());
            assertEquals(1, service.bookings.get(0).getUserId());
            assertEquals(101, service.bookings.get(0).getRoomNumber());
            assertEquals(2000, service.bookings.get(0).getTotalCost());
        }

        @Test
        @DisplayName("Should allow consecutive bookings for same room")
        void shouldAllowConsecutiveBookingsForSameRoom() {
            // Given
            service.setRoom(101, RoomType.STANDARD, 1000);
            service.setUser(1, 5000);
            service.setUser(2, 5000);
            service.bookRoom(1, 101, checkIn, checkOut);

            // When
            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.JULY, 3);
            Date consecutiveCheckIn = cal.getTime();
            cal.set(2026, Calendar.JULY, 5);
            Date consecutiveCheckOut = cal.getTime();
            service.bookRoom(2, 101, consecutiveCheckIn, consecutiveCheckOut);

            // Then
            assertEquals(2, service.bookings.size());
        }
    }

    @Nested
    @DisplayName("Room Booking - Validation Failures")
    class RoomBookingValidationFailures {

        @Test
        @DisplayName("Should reject booking when user has insufficient balance")
        void shouldRejectBookingWhenUserHasInsufficientBalance() {
            // Given
            service.setRoom(101, RoomType.STANDARD, 1000);
            service.setUser(1, 1000);

            // When
            service.bookRoom(1, 101, checkIn, checkOut);

            // Then
            assertEquals(0, service.bookings.size());
            assertEquals(1000, service.users.get(0).getBalance());
        }

        @Test
        @DisplayName("Should reject booking when user does not exist")
        void shouldRejectBookingWhenUserDoesNotExist() {
            // Given
            service.setRoom(101, RoomType.STANDARD, 1000);

            // When
            service.bookRoom(1, 101, checkIn, checkOut);

            // Then
            assertEquals(0, service.bookings.size());
        }

        @Test
        @DisplayName("Should reject booking when room does not exist")
        void shouldRejectBookingWhenRoomDoesNotExist() {
            // Given
            service.setUser(1, 5000);

            // When
            service.bookRoom(1, 101, checkIn, checkOut);

            // Then
            assertEquals(0, service.bookings.size());
        }

        @Test
        @DisplayName("Should reject booking when checkout date is before checkin date")
        void shouldRejectBookingWhenCheckoutDateIsBeforeCheckinDate() {
            // Given
            service.setRoom(101, RoomType.STANDARD, 1000);
            service.setUser(1, 5000);

            // When
            service.bookRoom(1, 101, checkOut, checkIn);

            // Then
            assertEquals(0, service.bookings.size());
        }

        @Test
        @DisplayName("Should reject booking when room is not available for requested period")
        void shouldRejectBookingWhenRoomIsNotAvailableForRequestedPeriod() {
            // Given
            service.setRoom(101, RoomType.STANDARD, 1000);
            service.setUser(1, 5000);
            service.setUser(2, 5000);
            service.bookRoom(1, 101, checkIn, checkOut);

            // When
            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.JULY, 2);
            Date overlapCheckIn = cal.getTime();
            cal.set(2026, Calendar.JULY, 4);
            Date overlapCheckOut = cal.getTime();
            service.bookRoom(2, 101, overlapCheckIn, overlapCheckOut);

            // Then
            assertEquals(1, service.bookings.size());
        }
    }

    @Nested
    @DisplayName("Data Ordering")
    class DataOrdering {

        @Test
        @DisplayName("Should maintain latest to oldest ordering for rooms")
        void shouldMaintainLatestToOldestOrderingForRooms() {
            // Given & When
            service.setRoom(101, RoomType.STANDARD, 1000);
            service.setRoom(102, RoomType.JUNIOR, 2000);
            service.setRoom(103, RoomType.SUITE, 3000);

            // Then
            assertEquals(103, service.rooms.get(0).getRoomNumber());
            assertEquals(102, service.rooms.get(1).getRoomNumber());
            assertEquals(101, service.rooms.get(2).getRoomNumber());
        }

        @Test
        @DisplayName("Should maintain latest to oldest ordering for users")
        void shouldMaintainLatestToOldestOrderingForUsers() {
            // Given & When
            service.setUser(1, 5000);
            service.setUser(2, 8000);
            service.setUser(3, 10000);

            // Then
            assertEquals(3, service.users.get(0).getUserId());
            assertEquals(2, service.users.get(1).getUserId());
            assertEquals(1, service.users.get(2).getUserId());
        }

        @Test
        @DisplayName("Should maintain latest to oldest ordering for bookings")
        void shouldMaintainLatestToOldestOrderingForBookings() {
            // Given
            service.setRoom(101, RoomType.STANDARD, 1000);
            service.setRoom(102, RoomType.JUNIOR, 2000);
            service.setUser(1, 10000);

            // When
            Calendar cal = Calendar.getInstance();

            cal.set(2026, Calendar.JULY, 1);
            Date booking1CheckIn = cal.getTime();
            cal.set(2026, Calendar.JULY, 2);
            Date booking1CheckOut = cal.getTime();
            service.bookRoom(1, 101, booking1CheckIn, booking1CheckOut);

            cal.set(2026, Calendar.JULY, 3);
            Date booking2CheckIn = cal.getTime();
            cal.set(2026, Calendar.JULY, 4);
            Date booking2CheckOut = cal.getTime();
            service.bookRoom(1, 102, booking2CheckIn, booking2CheckOut);

            // Then
            assertEquals(2, service.bookings.size());
            assertEquals(102, service.bookings.get(0).getRoomNumber());
            assertEquals(101, service.bookings.get(1).getRoomNumber());
        }
    }
}
