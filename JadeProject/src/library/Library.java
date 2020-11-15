package library;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import agents.Security;
import agents.Table;
import jade.core.*;
import jade.core.Runtime;
import jade.domain.JADEAgentManagement.KillAgent;
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

	private Agent librarian;
	private Agent[] securities;
	private Agent[][] tables;
	private Agent[] students;
	
	// Period of time, in milliseconds, the library is working.
	private int workingTime;

	public Library(String filename) {
		try{
			createContainers();

			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			workingTime = Integer.parseUnsignedInt(reader.readLine() + "000");

			int number_of_floors = Integer.parseUnsignedInt(reader.readLine());
			Logs.init(number_of_floors);
			securities = new Agent[number_of_floors];
			tables = new Agent[number_of_floors][];
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
				tables[i] = new Agent[num_tables];
				for(int j=0; j<num_tables; j++){
					tables[i][j] = new Table(floor);
					tablesContainer.acceptNewAgent("table_"+i+"_"+j, tables[i][j]).start();
				}
				securities[i] = new Security(floor, noise_tolerance);
				securitiesContainer.acceptNewAgent("security_" + i, securities[i]).start();
			}

			librarian = new Librarian();
			librariansContainer.acceptNewAgent("librarian", librarian).start();

			Random random = new Random();
			
			int number_of_students = Integer.parseUnsignedInt(reader.readLine());
			students = new Agent[number_of_students];
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
				int timeOfArrival = random.nextInt(this.workingTime) + 1000;
				
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
				students[i] = new Student(course, noise, action, timeOfArrival);
				studentsContainer.acceptNewAgent(nickname, students[i]).start();
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

	public void run(){
		//fazer calculos estatisticos

		//calcular satisfação de cada piso;
		//calcular quantos clientes foram expulsos
		//calcular ocupação média da biblioteca

		System.out.println("=======================================");
		System.out.println("==                                   ==");
		System.out.println("==        MULTI AGENT LIBRARY        ==");
		System.out.println("==                                   ==");
		System.out.println("=======================================");
		System.out.println(" ");
		System.out.println("Library working");
		int time = 0;

		double curr_total_occupancy = 0;
		int curr_total_iterations = 0;


		while(time < workingTime){
			try {
				Thread.sleep(1000);
				time += 1000;
				System.out.print(".");
				double occupancy = 0;
				int totalTables = 0;

				for (Agent[] table : tables) {
					for (Agent agent : table) {
						occupancy += (((Table) agent).isFree() ? 0 : 1);
						totalTables++;
					}
				}

				curr_total_occupancy += occupancy / totalTables;
				curr_total_iterations++;

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		double averageOccupancy = curr_total_occupancy / curr_total_iterations;

		int numberKicks = 0;
		for (Agent security: securities) {
			numberKicks += ((Security)security).getNumberKicks();
		}

		double satisfaction = 0;
		int countTables = 0;
		for (Agent[] table : tables) {
			for (Agent agent : table) {
				satisfaction += ((Table) agent).getSatisfaction();
				countTables++;
			}
		}

		double averageSatisfaction = satisfaction / countTables;

		System.out.println(" \n");
		System.out.println("=======================================");
		System.out.println("==                                   ==");
		System.out.println("==              RESULTS              ==");
		System.out.println("==                                   ==");
		System.out.println("=======================================");
		System.out.println("                                       ");
		System.out.println("-> Working time: " + workingTime/1000 + " seconds");
		System.out.println("-> Number of securities kicks: " + numberKicks);
		System.out.println("-> Average table satisfaction: " + (averageSatisfaction * 100) + "%");
		System.out.println("-> Average table occupancy: " + (averageOccupancy * 100) + "%");

	}

	public void shutdown() {

		try {
			librariansContainer.kill();
			securitiesContainer.kill();
			tablesContainer.kill();
			studentsContainer.kill();
			mainContainer.kill();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
}
