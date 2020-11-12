package library;


import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

	private static Runtime rt;
	private static ProfileImpl profile;
	private static ProfileImpl p1;
	private static ProfileImpl p2;
	private static AgentContainer mainContainer;
	private static AgentContainer securitiesContainer;
	private static AgentContainer tablesContainer;
	private static AgentContainer studentsContainer;
	
	private AgentController ac1;

	public Library(int noFloors, int noTables, int noStudents) {
		this.number_of_floors = noFloors;
		this.number_of_students = noStudents;	// students per table ??
		this.number_of_tables = noTables; //tables per floor

		this.rt = Runtime.instance();
		this.p = new ProfileImpl();
		this.p.setParameter(Profile.GUI, "true");
		this.cc =  this.rt.createMainContainer(this.p);

		try {
			this.ac1 = this.cc.acceptNewAgent("person1", new Librarian());
			this.ac1.start();
			
			this.cc.acceptNewAgent("person2", new Student("Paulo", "MIEIC", 2)).start();
		} catch (StaleProxyException e) {
			e.printStackTrace(); 
		}
	}
	
	public Library(String filename) throws FileNotFoundException {
		try{
			this.rt = Runtime.instance();
			this.profile = new ProfileImpl();
			this.profile.setParameter(Profile.GUI, "true");
			this.mainContainer = this.rt.createMainContainer(this.profile);

			BufferedReader reader = new BufferedReader(new FileReader(filename));

			this.number_of_floors = Integer.parseInt(reader.readLine());
			for(int i=0; i<this.number_of_floors; i++){
				String in = reader.readLine();
				String[] in_arr = in.split(" ");
				int num_tables = Integer.parseInt(in_arr[0]);
				String course = in_arr[1];
				int noise_tolerance = Integer.parseInt(in_arr[2]);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}


		try {
			this.ac1 = this.cc.acceptNewAgent("person1", new Librarian());
			this.ac1.start();

			this.cc.acceptNewAgent("person2", new Student("Paulo", "MIEIC", 2)).start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

	public static void createContainers() {
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
	
	private void createFloors(int noTables, int noStudents) {
		
	}

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length == 1) {
			new Library(args[0]);
		} else {
			System.err.println("Usage: java Library <filename>");
			System.exit(1);
		}
	}

	public

}
