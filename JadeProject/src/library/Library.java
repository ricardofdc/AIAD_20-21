package library;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import agents.Security;
import agents.Table;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;

import agents.Librarian;
import agents.Student;
import jade.wrapper.AgentContainer;


public class Library {

	private Runtime rt;
	private ProfileImpl mainProfile;
	private ProfileImpl librariansProfile;
	private ProfileImpl securitiesProfile;
	private ProfileImpl tablesProfile;
	private ProfileImpl studentsProfile;
	private AgentContainer mainContainer;
	private AgentContainer librariansContainer;
	private AgentContainer securitiesContainer;
	private AgentContainer tablesContainer;
	private AgentContainer studentsContainer;
	
	// Period of time, in milliseconds, the library is working.
	private int workingTime;

	public Library(String filename) {
		try{
			Logs.init();
			createContainers();

			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			workingTime = Integer.parseUnsignedInt(reader.readLine() + "000");

			int number_of_floors = Integer.parseUnsignedInt(reader.readLine());
			for(int i = 0; i< number_of_floors; i++){
				String in = reader.readLine();
				String[] in_arr = in.split(" ");
				if(in_arr.length != 3){
					System.err.println("Error on parse of file " + filename + ".");
					System.err.println("Floors must have 3 arguments: \"<n.tables> <course> <noise_tol>\".");
					System.exit(1);
				}
				int num_tables = Integer.parseUnsignedInt(in_arr[0]);
				String course = in_arr[1];
				int noise_tolerance = Integer.parseUnsignedInt(in_arr[2]);

				Floor floor = new Floor(i, course);
				securitiesContainer.acceptNewAgent("security_" + i, new Security(floor, noise_tolerance)).start();
				for(int j=0; j<num_tables; j++){
					tablesContainer.acceptNewAgent("table_"+i+"_"+j, new Table(floor)).start();
				}
			}

			librariansContainer.acceptNewAgent("librarian", new Librarian()).start();

			Random random = new Random();
			
			int number_of_students = Integer.parseUnsignedInt(reader.readLine());
			for(int i = 0; i< number_of_students; i++) {
				String in = reader.readLine();
				String[] in_arr = in.split(" ");
				if(in_arr.length != 4){
					System.err.println("Error on parse of file " + filename + ".");
					System.err.println("Students must have 4 arguments: \"<name> <course> <noise> <action>\".");
					System.err.println("Noise must be between 1 and 10.");
					System.err.println("Action = 0 -> student wants a table.");
					System.err.println("Action = 1 -> student wants a book.");
					System.exit(1);
				}

				String name = in_arr[0];
				String course = in_arr[1];
				int noise = Integer.parseUnsignedInt(in_arr[2]);
				int action = Integer.parseUnsignedInt(in_arr[3]);
				int timeOfArrival = random.nextInt(this.workingTime - 2) + 2;
				
				String nickname;
				if (i<10) {
					nickname = "00"+i+"_"+name;	
				}
				else if (i<100){
					nickname = "0"+i+"_"+name;
				}
				else{
					nickname = i+"_"+name;
				}
				
				studentsContainer.acceptNewAgent(nickname, new Student(course, noise, action, timeOfArrival)).start();
			}
			reader.close();
		} catch (IOException | StaleProxyException e) {
			e.printStackTrace();
		}
	}

	public void createContainers() {
		rt = Runtime.instance();
		mainProfile = new ProfileImpl();
		mainProfile.setParameter(Profile.GUI, "true");
		mainContainer = rt.createMainContainer(mainProfile);

		librariansProfile = new ProfileImpl();
		librariansProfile.setParameter(Profile.CONTAINER_NAME, "Librarians");
		librariansContainer = rt.createAgentContainer(librariansProfile);

		securitiesProfile = new ProfileImpl();
		securitiesProfile.setParameter(Profile.CONTAINER_NAME, "Securities");
		securitiesContainer = rt.createAgentContainer(securitiesProfile);

		tablesProfile = new ProfileImpl();
		tablesProfile.setParameter(Profile.CONTAINER_NAME, "Tables");
		tablesContainer = rt.createAgentContainer(tablesProfile);

		studentsProfile = new ProfileImpl();
		studentsProfile.setParameter(Profile.CONTAINER_NAME, "Students");
		studentsContainer = rt.createAgentContainer(studentsProfile);
	}
}
