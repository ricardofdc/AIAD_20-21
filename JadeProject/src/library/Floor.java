package library;

public class Floor {
    private final int floor_nr;
    private final String course;

    public Floor(int floor_nr, String course) {
        this.floor_nr = floor_nr;
        this.course = course;
    }

    public int getfloorNr() {
        return floor_nr;
    }

    public String getCourse() {
        return course;
    }
}
