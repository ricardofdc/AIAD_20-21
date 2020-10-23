import agents.Student;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Library {

    private final int number_of_floors;
    private final int number_of_students;
    private final int number_of_tables;
    private final Runtime rt;
    private final ProfileImpl p;
    private final AgentContainer cc;
    private final AgentController ac1;

    public Library(int noFloors, int noStudents, int noTables){
        this.number_of_floors = noFloors;
        this.number_of_students = noStudents;
        this.number_of_tables = noTables; //tables per floor

        this.rt = Runtime.instance();
        this.p = new ProfileImpl();
        this.p.setParameter(Profile.GUI, "true");
        this.cc = this.rt.createMainContainer(this.p);

        try {
            this.ac1 = this.cc.acceptNewAgent("person1", new Student("name1", "MIEIC", 2));
            this.ac1.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Library l1 = new Library(/*...*/);

    }

}
