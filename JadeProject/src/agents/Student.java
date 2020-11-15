package agents;

import agentBehaviours.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import library.Logs;

import java.util.ArrayList;

public class Student extends Agent {

    private ArrayList<AID> librarian;

    private final String course;
    private final int noise;
    private final int action; // 0 -> get table; 1 -> get book
    private final int timeOfArrival;
    private AID tableAID;

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

    public ArrayList<AID> getLibrarianAID() {
        return librarian;
    }

    public void setup() {
    	super.setup();

    	registerStudent();

        addBehaviour(new WakerBehaviour(this, timeOfArrival) {
            @Override
            protected void onWake() {
                super.onWake();

                addBehaviour(new StudentRequestBehaviour());
                addBehaviour(new StudentListenBehaviour());

            }
        });
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
            Logs.write(this.getName() + " REGISTERED AS STUDENT", "student");
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
    }


    protected void takeDown() {
        try {
            DFService.deregister(this);
            Logs.write(this.getName() + " TAKEN DOWN AND UNREGISTERED FROM DFSERVICE", "student");
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }


}
