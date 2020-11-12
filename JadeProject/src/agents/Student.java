package agents;

import agentBehaviours.*;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Student extends Agent {


    private final String course;
    private final int noise;
    private final int action; // 0 -> get table; 1 -> get book

    public Student( String course, int noise, int action){
        this.course = course;
        this.noise = noise;
        this.action = action;

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
    	
    	DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("receptionist");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			for(int i=0; i<result.length; ++i) {
				System.out.println("Found " + result[i].getName());
			}
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		
        addBehaviour(new WorkingBehaviour());
        addBehaviour(new ListeningBehaviour(this));

        System.out.println(getLocalName() + ": starting to work!");
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }


}
