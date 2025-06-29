package skypay.model;

public class Room {
    private final int roomNumber;
    private RoomType roomType;
    private int roomPricePerNight;

    public Room(int roomNumber, RoomType roomType, int roomPricePerNight) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomPricePerNight = roomPricePerNight;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public int getRoomPricePerNight() {
        return roomPricePerNight;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setRoomPricePerNight(int roomPricePerNight) {
        this.roomPricePerNight = roomPricePerNight;
    }

    @Override
    public String toString() {
        return "Room{Number=" + roomNumber + ", Type=" + roomType + ", Price/Night=" + roomPricePerNight + "}";
    }
}