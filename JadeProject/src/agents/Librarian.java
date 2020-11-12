package agents;

import java.util.ArrayList;

import agentBehaviours.ListeningBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Librarian extends Agent {
	
	private ArrayList<AID> floors;

    public void setup() {    	
    	registerLibrarian();
    	updateFloorsAID();
    	
    	addBehaviour(new ListeningBehaviour(this));
    }
    
    private void registerLibrarian() {
    	DFAgentDescription dfd = new DFAgentDescription();
    	ServiceDescription sd = new ServiceDescription();
    	
		sd.setType("receptionist");
		sd.setName(getLocalName());
		
		dfd.setName(getAID());
		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
    }
    
    private void updateFloorsAID() {
    	DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		
		sd.setType("floor");
		dfd.addServices(sd);
		
		try {
			DFAgentDescription[] result = DFService.search(this, dfd);
			
			for(int i = 0; i < result.length; i++) {
				System.out.println("Found " + result[i].getName());
				floors.add(result[i].getName());
			}
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
    }

}
