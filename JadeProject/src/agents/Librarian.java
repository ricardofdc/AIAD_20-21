package agents;

import java.util.ArrayList;

import agentBehaviours.LibrarianListenBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import library.Logs;

public class Librarian extends Agent {
	
	private ArrayList<AID> floorsSecurity;

    public void setup() {    	
    	registerLibrarian();

		addBehaviour(new LibrarianListenBehaviour());

    }

    public ArrayList<AID> getFloorsSecurity(){
    	return floorsSecurity;
	}
    
    private void registerLibrarian() {
    	DFAgentDescription dfd = new DFAgentDescription();
    	ServiceDescription sd = new ServiceDescription();
		sd.setType("librarian");
		sd.setName(getLocalName());
		dfd.setName(getAID());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
			Logs.write(this.getName() + " REGISTERED AS LIBRARIAN", "librarian");
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
    }

	protected void takeDown() {
		try {
			DFService.deregister(this);
			Logs.write(this.getName() + " TAKEN DOWN AND UNREGISTERED FROM DFSERVICE", "librarian");
		} catch(FIPAException e) {
			e.printStackTrace();
		}
	}

}
