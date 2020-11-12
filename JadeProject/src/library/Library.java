package library;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import agents.Security;
import agents.Table;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;

import agents.Librarian;
import agents.Student;
import jade.wrapper.AgentContainer;


public class Library {

	private int number_of_floors = 0;
	private int number_of_students = 0;
	private int number_of_tables = 0;

	private Runtime rt;
	private ProfileImpl profile;
	private ProfileImpl p1;
	private ProfileImpl p2;
	private AgentContainer mainContainer;
	private AgentContainer securitiesContainer;
	private AgentContainer tablesContainer;
	private AgentContainer studentsContainer;


	public Library(String filename) {
		try{
			createContainers();

			BufferedReader reader = new BufferedReader(new FileReader(filename));

			number_of_floors = Integer.parseInt(reader.readLine());
			for(int i=0; i<number_of_floors; i++){
				String in = reader.readLine();
				String[] in_arr = in.split(" ");
				if(in_arr.length != 3){
					System.err.println("Error on parse of file " + filename + ".");
					System.err.println("Floors must have 3 arguments: \"<n.tables> <course> <noise_tol>\".");
					System.exit(1);
				}
				int num_tables = Integer.parseInt(in_arr[0]);
				String course = in_arr[1];
				int noise_tolerance = Integer.parseInt(in_arr[2]);

				Floor floor = new Floor(i, course);
				securitiesContainer.acceptNewAgent("security_" + i, new Security(floor, noise_tolerance)).start();
				for(int j=0; j<num_tables; j++){
					tablesContainer.acceptNewAgent("table_"+i+"_"+j, new Table(floor)).start();
				}
			}

			number_of_students = Integer.parseInt(reader.readLine());
			for(int i=0; i<number_of_students; i++){
				String in = reader.readLine();
				String[] in_arr = in.split(" ");
				if(in_arr.length != 4){
					System.err.println("Error on parse of file " + filename + ".");
					System.err.println("Students must have 4 arguments: \"<name> <course> <noise> <action>\".");
					System.err.println("Action = 0 -> student wants a table.");
					System.err.println("Action = 1 -> student wants a book.");
					System.exit(1);
				}

				String name = in_arr[0];
				String course = in_arr[1];
				int noise = Integer.parseInt(in_arr[2]);
				int action = Integer.parseInt(in_arr[3]);
				if (i<10) {
					studentsContainer.acceptNewAgent("00"+i+"_"+name, new Student(course, noise, action)).start();
				}
				else if (i<100){
					studentsContainer.acceptNewAgent("0"+i+"_"+name, new Student(course, noise, action)).start();
				}
				else{
					studentsContainer.acceptNewAgent(i+"_"+name, new Student(course, noise, action)).start();
				}
			}

			reader.close();
		} catch (IOException | StaleProxyException e) {
			e.printStackTrace();
		}



	}

	public void createContainers() {
		rt = Runtime.instance();
		profile = new ProfileImpl();
		profile.setParameter(Profile.GUI, "true");
		mainContainer = rt.createMainContainer(profile);

		p1 = new ProfileImpl();
		p1.setParameter(Profile.CONTAINER_NAME, "Securities");
		securitiesContainer = rt.createAgentContainer(p1);

		p2 = new ProfileImpl();
		p2.setParameter(Profile.CONTAINER_NAME, "Tables");
		tablesContainer = rt.createAgentContainer(p2);

		p2 = new ProfileImpl();
		p2.setParameter(Profile.CONTAINER_NAME, "Students");
		studentsContainer = rt.createAgentContainer(p2);
	}

}
