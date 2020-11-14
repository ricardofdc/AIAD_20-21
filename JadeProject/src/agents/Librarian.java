package agents;

import java.util.ArrayList;

import agentBehaviours.ListeningBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import library.Logs;

public class Librarian extends Agent {
	
	private ArrayList<AID> floorsSecurity;

    public void setup() {    	
    	registerLibrarian();
		getFloorsSecurityAID();
    	
    	//addBehaviour(new ListeningBehaviour(this));
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
    
    private void getFloorsSecurityAID() {
    	addBehaviour(new WakerBehaviour(this, 1000) {
			@Override
			protected void onWake() {
				super.onWake();

				DFAgentDescription dfd = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();

				sd.setType("security");
				dfd.addServices(sd);

				try {
					DFAgentDescription[] result = DFService.search(myAgent, dfd);
					floorsSecurity = new ArrayList<AID>();

					for (DFAgentDescription agent : result) {
						Logs.write(this.myAgent.getName() + " FOUND " + agent.getName(), "librarian");
						floorsSecurity.add(agent.getName());
					}
				} catch(FIPAException fe) {
					fe.printStackTrace();
				}
			}
		});
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
