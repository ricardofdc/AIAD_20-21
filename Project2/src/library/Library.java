package library;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import agents.Security;
import agents.Table;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Agent;
import sajas.core.Runtime;

import agents.Librarian;
import agents.Student;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DGrid;


public class Library extends Repast3Launcher {

	private static final boolean BATCH_MODE = false;

	private static final int WORKING_TIME = 15000;
	private static final int AVG_NOISE_TOLERANCE = 8;
	private static final int AVG_STUDENT_NOISE = 8;
	private final int N_FLOORS = 5;
	private final String[] COURSES = {"MIEIC", "MIEM", "MIEGI", "MIEC", "MIEQ"};
	private final int N_TABLES_PER_FLOOR = 5;
	private final int N_STUDENTS = 20;
	public static final boolean USE_RESULTS_COLLECTOR = true;


	private ContainerController mainContainer;
	private ContainerController librariansContainer;
	private ContainerController securitiesContainer;
	private ContainerController tablesContainer;
	private ContainerController studentsContainer;

	private Agent librarian;
	private Agent[] securities;
	private Agent[][] tables;
	private List<Table> tablesList;
	private Agent[] students;

	Random random = new Random();

	private String filename;

	private DisplaySurface dsurf;
	private int WIDTH = 20, HEIGHT = 20;
	private Object2DGrid library2DGrid;

	public Library(String filename) {
		this.filename = filename;
	}

	/*
	private void run(){
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
		while(time <= workingTime){
			try {
				Thread.sleep(300);
				time += 300;
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

	 */

	private void shutdown() {
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

	@Override
	public void setup() {
		super.setup();

		if (dsurf != null) dsurf.dispose();
		dsurf = new DisplaySurface(this, "Library Display");
		registerDisplaySurface("Library Display", dsurf);
	}

	@Override
	public void begin() {
		buildModel();
		if (!BATCH_MODE) {
			buildAndScheduleDisplay();
		}

		super.begin();
	}

	private void buildModel() {
		library2DGrid = new Object2DGrid(WIDTH, HEIGHT);
		tablesList = new ArrayList<Table>();
	}

	private void buildAndScheduleDisplay() {
		Object2DDisplay libraryDisplay = new Object2DDisplay(library2DGrid);
		libraryDisplay.setObjectList(tablesList);
		dsurf.addDisplayableProbeable(libraryDisplay, "Tables");
		addSimEventListener(dsurf);

		dsurf.display();

		getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
	}

	@Override
	protected void launchJADE() {

		Runtime rt = Runtime.instance();
		Profile mainProfile = new ProfileImpl();
		mainContainer = rt.createMainContainer(mainProfile);

		Profile librariansProfile = new ProfileImpl();
		librariansProfile.setParameter(Profile.CONTAINER_NAME, "Librarians");
		librariansContainer = rt.createAgentContainer(librariansProfile);

		Profile securitiesProfile = new ProfileImpl();
		securitiesProfile.setParameter(Profile.CONTAINER_NAME, "Securities");
		securitiesContainer = rt.createAgentContainer(securitiesProfile);

		Profile tablesProfile = new ProfileImpl();
		tablesProfile.setParameter(Profile.CONTAINER_NAME, "Tables");
		tablesContainer = rt.createAgentContainer(tablesProfile);

		Profile studentsProfile = new ProfileImpl();
		studentsProfile.setParameter(Profile.CONTAINER_NAME, "Students");
		studentsContainer = rt.createAgentContainer(studentsProfile);

		launchAgents();

		// TODO: add an agent with run comportment


		//run();
		//shutdown();
	}

	private void launchAgents() {

		try{

			Logs.init(N_FLOORS);
			securities = new Agent[N_FLOORS];
			tables = new Agent[N_FLOORS][];
			for(int i = 0; i< N_FLOORS; i++){
				int noise_tolerance = AVG_NOISE_TOLERANCE - 2 + random.nextInt(5); // input[2 .. 8]
				Floor floor = new Floor(i, COURSES[i]);
				tables[i] = new Agent[N_TABLES_PER_FLOOR];
				for(int j=0; j<N_TABLES_PER_FLOOR; j++){
					Table table = new Table(floor, i*2, j*2);

					tables[i][j] = table;
					tablesList.add(table);
					tablesContainer.acceptNewAgent("table_"+i+"_"+j, table).start();
				}
				securities[i] = new Security(floor, noise_tolerance);
				securitiesContainer.acceptNewAgent("security_" + i, securities[i]).start();
			}

			librarian = new Librarian();
			librariansContainer.acceptNewAgent("librarian", librarian).start();


			students = new Agent[N_STUDENTS];
			for(int i = 0; i< N_STUDENTS; i++) {

				int noise = AVG_STUDENT_NOISE - 2 + random.nextInt(5);
				int action = 0;
				int timeOfArrival = random.nextInt(WORKING_TIME);

				String nickname;
				if (i<10) {
					nickname = "00"+i+"_student";
				}
				else if (i<100){
					nickname = "0"+i+"_student";
				}
				else{
					nickname = i+"_student";
				}
				int course_n = random.nextInt(N_FLOORS);
				students[i] = new Student(COURSES[course_n], noise, action, timeOfArrival);
				studentsContainer.acceptNewAgent(nickname, students[i]).start();
			}
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[] getInitParam() {
		return new String[0];
	}

	@Override
	public String getName() {
		return "AIAD - Library Service";
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java Library <filename>");
			System.exit(1);
		}

		if(!new File(args[0]).exists()) {
			System.err.println("File " + args[0] + " not found.");
			System.exit(1);
		}

		SimInit init = new SimInit();
		init.setNumRuns(1);   // works only in batch mode
		init.loadModel(new Library(args[0]), null, BATCH_MODE);
	}
}
