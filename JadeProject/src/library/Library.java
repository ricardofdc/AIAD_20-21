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
			Random random = new Random();
			createContainers();

			BufferedReader reader = new BufferedReader(new FileReader(filename));

			//read first line: working time of library
			workingTime = Integer.parseUnsignedInt(reader.readLine() + "000");

			//read security related lines
			String security_input = reader.readLine();
			String[] security_input_array = security_input.split(" ");
			int number_of_floors = Integer.parseUnsignedInt(security_input_array[0]);
			int average_tolerance = Integer.parseUnsignedInt(security_input_array[1]);
			Logs.init(number_of_floors);
			securities = new Agent[number_of_floors];
			tables = new Agent[number_of_floors][];
			for(int i = 0; i< number_of_floors; i++){
				String in = reader.readLine();
				String[] in_arr = in.split(" ");
				if(in_arr.length != 2){
					System.err.println("Error on parse of file " + filename + ".");
					System.err.println("Floors must have 2 arguments: \"<n.tables> <course>\".");
					System.exit(1);
				}
				int num_tables = Integer.parseUnsignedInt(in_arr[0]);
				String course = in_arr[1];
				int noise_tolerance = average_tolerance - 2 + random.nextInt(5); // input[2 .. 8]

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


			//read student related lines
			String student_input = reader.readLine();
			String[] student_input_array = student_input.split(" ");
			int number_of_students = Integer.parseUnsignedInt(student_input_array[0]);
			int average_noise = Integer.parseUnsignedInt(student_input_array[1]);
			students = new Agent[number_of_students];
			for(int i = 0; i< number_of_students; i++) {
				String in = reader.readLine();
				String[] in_arr = in.split(" ");
				if(in_arr.length != 2){
					System.err.println("Error on parse of file " + filename + ".");
					System.err.println("Students must have 2 arguments: \"<name> <course>\".");
					System.exit(1);
				}

				String name = in_arr[0];
				String course = in_arr[1];
				int noise = average_noise - 2 + random.nextInt(5);
				int action = 0;
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


		//get average students noise
		double totalNoise = 0;
		int numStudents = 0;
		for(Agent agent: students){
			totalNoise += ((Student)agent).getNoise();
			numStudents++;
		}
		double averageStudentsNoise = totalNoise / numStudents;

		// get runtime tables information
		double curr_total_occupancy = 0;
		double curr_total_satisfaction = 0;
		int curr_total_iterations = 0;
		int time = 0;
		while(time < workingTime){
			try {
				Thread.sleep(1000);
				time += 1000;
				System.out.print(".");
				double occupancy = 0;
				double satisfaction = 0;
				int numTables_ocupancy = 0;
				int numTables_satisfaction = 0;

				for (Agent[] table : tables) {
					for (Agent agent : table) {
						occupancy += (((Table) agent).isFree() ? 0 : 1);
						numTables_ocupancy++;
						if(!((Table)agent).isFree()){
							satisfaction += ((Table) agent).getSatisfaction();
							numTables_satisfaction++;
						}
					}
				}
				if(numTables_satisfaction != 0){
					curr_total_satisfaction += satisfaction / numTables_satisfaction;
				}
				curr_total_occupancy += occupancy / numTables_ocupancy;
				curr_total_iterations++;

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		double averageOccupancy = curr_total_occupancy / curr_total_iterations;
		double averageSatisfaction = curr_total_satisfaction / curr_total_iterations;

		//get number of security kicks and average securities noise tolerance
		int numberKicks = 0;
		double totalNoiseTolerance = 0;
		int numberSecurities = 0;
		for (Agent security: securities) {
			numberKicks += ((Security)security).getNumberKicks();
			totalNoiseTolerance += ((Security)security).getNoiseTolerance();
			numberSecurities++;
		}
		double averageNoiseTolerance = totalNoiseTolerance / numberSecurities;


		System.out.println(" \n");
		System.out.println("=======================================");
		System.out.println("==                                   ==");
		System.out.println("==              RESULTS              ==");
		System.out.println("==                                   ==");
		System.out.println("=======================================");
		System.out.println("                                       ");
		System.out.println("-> Working time: " + workingTime/1000 + " seconds");
		System.out.println("-> Average securities noise tolerance: " + averageNoiseTolerance);
		System.out.println("-> Average students noise: " + averageStudentsNoise);
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
