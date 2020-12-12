package library;

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
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DGrid;


public class Library extends Repast3Launcher {

	private static final boolean BATCH_MODE = false;

	private int WORKING_TIME = 150;
	private int AVG_NOISE_TOLERANCE = 5;
	private int AVG_STUDENT_NOISE = 5;
	private int N_FLOORS = 5;
	private final String[] COURSES = {"MIEIC", "MIEM", "MIEGI", "MIEC", "MIEQ", "MIB", "MIEA", "MIEEC", "MI:EF", "MIEMM"};
	private int N_TABLES_PER_FLOOR = 10;
	private int N_STUDENTS = 100;
	private int N_LIBRARIANS = 3;


	private ContainerController mainContainer;
	private ContainerController librariansContainer;
	private ContainerController securitiesContainer;
	private ContainerController tablesContainer;
	private ContainerController studentsContainer;

	private Agent[] librarians;
	private Agent[] securities;
	private Agent[][] tables;
	private List<Drawable> drawablesList;
	private Agent[] students;

	Random random = new Random();

	private DisplaySurface dsurf;
	private int WIDTH = N_FLOORS * 15, HEIGHT = N_TABLES_PER_FLOOR * 4 + 50;
	private Object2DGrid library2DGrid;
	private OpenSequenceGraph plot;


	public Library() {
	}

	public void setWORKING_TIME(int WORKING_TIME){
		this.WORKING_TIME = WORKING_TIME;
	}

	public int getWORKING_TIME(){
		return WORKING_TIME;
	}

	public void setAVG_NOISE_TOLERANCE(int AVG_NOISE_TOLERANCE){
		this.AVG_NOISE_TOLERANCE = AVG_NOISE_TOLERANCE;
	}

	public int getAVG_NOISE_TOLERANCE(){
		return AVG_NOISE_TOLERANCE;
	}

	public void setAVG_STUDENT_NOISE(int AVG_STUDENT_NOISE){
		this.AVG_STUDENT_NOISE = AVG_STUDENT_NOISE;
	}

	public int getAVG_STUDENT_NOISE(){
		return AVG_STUDENT_NOISE;
	}

	public void setN_FLOORS(int N_FLOORS){
		this.N_FLOORS = N_FLOORS;
	}

	public int getN_FLOORS(){
		return N_FLOORS;
	}

	public void setN_TABLES_PER_FLOOR(int N_TABLES_PER_FLOOR){
		this.N_TABLES_PER_FLOOR = N_TABLES_PER_FLOOR;
	}

	public int getN_TABLES_PER_FLOOR(){
		return N_TABLES_PER_FLOOR;
	}

	public void setN_STUDENTS(int N_STUDENTS){
		this.N_STUDENTS = N_STUDENTS;
	}

	public int getN_STUDENTS(){
		return N_STUDENTS;
	}

	public void setN_LIBRARIANS(int N_LIBRARIANS){
		this.N_LIBRARIANS = N_LIBRARIANS;
	}

	public int getN_LIBRARIANS(){
		return N_LIBRARIANS;
	}

	@Override
	public String[] getInitParam() {
		return new String[] {"WORKING_TIME",
				"AVG_NOISE_TOLERANCE",
				"AVG_STUDENT_NOISE",
				"N_FLOORS",
				"N_TABLES_PER_FLOOR",
				"N_STUDENTS",
				"N_LIBRARIANS"};
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
		drawablesList = new ArrayList<Drawable>();
	}

	private void buildAndScheduleDisplay() {

		//agents

		Object2DDisplay libraryDisplay = new Object2DDisplay(library2DGrid);
		libraryDisplay.setObjectList(drawablesList);
		dsurf.addDisplayableProbeable(libraryDisplay, "Tables");
		addSimEventListener(dsurf);

		dsurf.display();

		// graph
		if (plot != null) plot.dispose();
		plot = new OpenSequenceGraph("Library performance", this);
		plot.setAxisTitles("time", "%");

		plot.addSequence("Tables Occupation", new Sequence() {
			public double getSValue() {
				// iterate through consumers
				double occupation = 0.0;
				for(int i = 0; i < tables.length; i++) {
					for(int j=0; j< tables[i].length; j++){
						if(!((Table)tables[i][j]).isFree())
							occupation++;
					}
				}
				return occupation / (N_FLOORS*N_TABLES_PER_FLOOR) * 100;
			}
		});

		plot.addSequence("Student Satisfaction", new Sequence() {
			public double getSValue() {
				double occupancy = 0;
				double satisfaction = 0;
				for (Agent[] table : tables) {
					for (Agent agent : table) {
						occupancy += (((Table) agent).isFree() ? 0 : 1);
						if(!((Table)agent).isFree()){
							satisfaction += ((Table) agent).getSatisfaction();
						}
					}
				}
				if(occupancy != 0){
					return satisfaction / occupancy * 100;
				}
				return 100;
			}
		});

		plot.addSequence("Kicked students", new Sequence() {
			public double getSValue() {
				double numberKicks = 0;
				int num_students = 0;
				for(Agent student: students){
					if(((Student)student).getSeated()){
						num_students++;
					}
				}
				for (Agent security: securities) {
					numberKicks += ((Security)security).getNumberKicks();
				}
				return numberKicks / num_students * 100;
			}
		});

		plot.addSequence("Seated students", new Sequence() {
			public double getSValue() {
				double seated = 0;
				for(Agent student: students){
					if(((Student)student).getSeated()){
						seated++;
					}
				}
				return seated / N_STUDENTS * 100;
			}
		});


		plot.display();

		getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
		getSchedule().scheduleActionAtInterval(100, plot, "step", Schedule.LAST);
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
					Table table = new Table(floor, 10 + i*10, 5 + j*3);

					tables[i][j] = table;
					drawablesList.add(table);
					tablesContainer.acceptNewAgent("table_"+i+"_"+j, table).start();
				}
				securities[i] = new Security(floor, noise_tolerance);
				securitiesContainer.acceptNewAgent("security_" + i, securities[i]).start();
			}

			librarians = new Agent[N_LIBRARIANS];
			for(int i=0; i<N_LIBRARIANS; i++){
				librarians[i] = new Librarian();
				librariansContainer.acceptNewAgent("librarian" + i, librarians[i]).start();
			}

			students = new Agent[N_STUDENTS];
			for(int i = 0; i< N_STUDENTS; i++) {

				int noise = AVG_STUDENT_NOISE - 2 + random.nextInt(5);
				int action = 0;
				int timeOfArrival = random.nextInt(WORKING_TIME * 1000);

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
				students[i] = new Student(COURSES[course_n], noise, action, timeOfArrival, i+1, N_TABLES_PER_FLOOR);
				drawablesList.add((Drawable) students[i]);
				studentsContainer.acceptNewAgent(nickname, students[i]).start();
			}
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "AIAD - Library Service";
	}

	public static void main(String[] args) {

		SimInit init = new SimInit();
		init.setNumRuns(1);   // works only in batch mode
		init.loadModel(new Library(), null, BATCH_MODE);
	}
}
