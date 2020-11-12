package library;

public class Floor {
    private final int id;
    private final String course;

    public Floor(int id, String course) {
        this.id = id;
        this.course = course;
    }

    public int getId() {
        return id;
    }

    public String getCourse() {
        return course;
    }
}
