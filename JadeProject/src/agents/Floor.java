package agents;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Floor extends Agent {
	
	private ArrayList<AID> seats;
	
	public void setup() {
		registerFloor();
		updateSeatsAID();
	}
	
	private void registerFloor() {
		DFAgentDescription dfd = new DFAgentDescription();
    	ServiceDescription sd = new ServiceDescription();
    	
		sd.setType("floor");
		sd.setName(getLocalName());
		
		dfd.setName(getAID());
		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	private void updateSeatsAID() {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		
		sd.setType("seat");
		dfd.addServices(sd);
		
		try {
			DFAgentDescription[] result = DFService.search(this, dfd);
			
			for(int i = 0; i < result.length; i++) {
				seats.add(result[i].getName());
			}
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}
}
