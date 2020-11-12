package library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.LoadSettingsBuilder;

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
	
	//private final ArrayList<>

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
	
	public Library(String yamlFilename) throws FileNotFoundException {
		rt = null;
		p = null;
		cc = null;
		this.number_of_floors = 1;
		this.number_of_students = 1;
		this.number_of_tables = 1;
		
		System.out.println(yamlFilename);
		
		File file = new File(yamlFilename);
		InputStream inputStream = (InputStream) new FileInputStream(file);
		
		LoadSettings settings = LoadSettings.builder().setLabel("Custom user configuration").build();
		Load load = new Load(settings);
		Object list = load.loadFromInputStream(inputStream);
		System.out.println(list);
	}
	
	private void createFloors(int noTables, int noStudents) {
		
	}

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length == 3) {
			new Library(Integer.parseUnsignedInt(args[0]), Integer.parseUnsignedInt(args[1]), Integer.parseUnsignedInt(args[2]));
		} else if (args.length == 1) {
			new Library(args[0]);
		} else {
			System.err.println("Usage: java Library <num_floors> <num_tables_per_floor> <num_students_per_table>");
			System.exit(1);
		}
	}

}
