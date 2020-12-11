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
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Drawable;
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
	private List<Drawable> drawablesList;
	private Agent[] students;

	Random random = new Random();

	private DisplaySurface dsurf;
	private int WIDTH = N_TABLES_PER_FLOOR * 15, HEIGHT = N_STUDENTS * 4;
	private Object2DGrid library2DGrid;

	public Library() {
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
		Object2DDisplay libraryDisplay = new Object2DDisplay(library2DGrid);
		libraryDisplay.setObjectList(drawablesList);
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
					Table table = new Table(floor, 10 + i*10, 5 + j*3);

					tables[i][j] = table;
					drawablesList.add(table);
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
				students[i] = new Student(COURSES[course_n], noise, action, timeOfArrival, i+1);
				drawablesList.add((Drawable) students[i]);
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

		SimInit init = new SimInit();
		init.setNumRuns(1);   // works only in batch mode
		init.loadModel(new Library(), null, BATCH_MODE);
	}
}
