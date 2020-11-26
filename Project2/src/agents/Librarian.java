package agents;

import agentBehaviours.LibrarianListenBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import library.Logs;

public class Librarian extends Agent {

    public void setup() {    	
    	registerLibrarian();
		addBehaviour(new LibrarianListenBehaviour());
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
    	super.takeDown();
		try {
			DFService.deregister(this);
			Logs.write(this.getName() + " TAKEN DOWN AND UNREGISTERED FROM DFSERVICE", "librarian");
		} catch(FIPAException e) {
			e.printStackTrace();
		}
	}

}
