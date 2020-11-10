package library;

import agents.Librarian;
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
	
	private final ArrayList<>

	private final Runtime rt;
	private final ProfileImpl p;
	private final AgentContainer cc;
	
	private AgentController ac1;

	public Library(int noFloors, int noTables, int noStudents) {
		this.number_of_floors = noFloors;
		this.number_of_students = noStudents;	// students per table ??
		this.number_of_tables = noTables; //tables per floor

		
		
		this.rt = Runtime.instance();
		this.p = new ProfileImpl();
		this.p.setParameter(Profile.GUI, "true");
		this.cc = this.rt.createMainContainer(this.p);

		try {
			this.ac1 = this.cc.acceptNewAgent("person1", new Librarian());
			this.ac1.start();
			
			this.cc.acceptNewAgent("person2", new Student("Paulo", "MIEIC", 2)).start();
		} catch (StaleProxyException e) {
			e.printStackTrace(); 
		}

	}
	
	private void createFloors(int noTables, int noStudents) {
		
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("Usage: java Library <num_floors> <num_tables_per_floor> <num_students_per_table>");
			System.exit(1);
		}

		Library l1 = new Library(Integer.parseUnsignedInt(args[0]), Integer.parseUnsignedInt(args[1]), Integer.parseUnsignedInt(args[2]));
	}

}
