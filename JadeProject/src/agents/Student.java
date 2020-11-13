package agents;

import agentBehaviours.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;

public class Student extends Agent {

    private ArrayList<AID> librarian;

    private final String course;
    private final int noise;
    private final int action; // 0 -> get table; 1 -> get book
    private final int timeOfArrival;

    public Student( String course, int noise, int action, int timeOfArrival) {
        this.course = course;
        this.noise = noise;
        this.action = action;
        this.timeOfArrival = timeOfArrival;	//milliseconds

        //later we can make noise and course be random
    }

    public String getCourse() {
        return course;
    }

    public int getNoise() {
        return noise;
    }

    public int getAction() {
        return action;
    }

    public void setup() {
    	super.setup();

    	registerStudent();
    	getLibrarianAID();
		
    	addBehaviour(new StudentRequestTable(this, (long) timeOfArrival));
        //addBehaviour(new WorkingBehaviour());
        //addBehaviour(new ListeningBehaviour(this));

        //System.out.println(getLocalName() + ": starting to work!");
    }

    private void registerStudent(){
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("student");
        sd.setName(this.getName()); // name: 008_barbara@192.168.1.91:1099/JADE
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void getLibrarianAID() {
        addBehaviour(new WakerBehaviour(this, 1000) {
            @Override
            protected void onWake() {
                super.onWake();

                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();

                sd.setType("librarian");
                dfd.addServices(sd);

                try {
                    DFAgentDescription[] result = DFService.search(myAgent, dfd);
                    librarian = new ArrayList<AID>();

                    for(int i = 0; i < result.length; i++) {
                        System.out.println("Student found " + result[i].getName());
                        librarian.add(result[i].getName());
                    }
                } catch(FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }


}
