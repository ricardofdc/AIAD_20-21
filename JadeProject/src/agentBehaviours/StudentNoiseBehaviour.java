package agentBehaviours;

import java.util.Random;

import agents.Student;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class StudentNoiseBehaviour extends TickerBehaviour {
	
	private Random rnd = new Random();

	private AID floorSecurity;

	public StudentNoiseBehaviour(Agent a, long period, AID table) {
		super(a, period);
		
		String securityName = table.getName().substring(table.getName().indexOf("@"));
		securityName = "security_"
				+ table.getLocalName().split("_")[1]
				+ securityName;
		
		floorSecurity = getFloorSecurity(securityName);
	}
	
	private AID getFloorSecurity(String securityName) {
		DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();

        sd.setType("security");
        sd.setName(securityName);
        dfd.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(myAgent, dfd);

            for (DFAgentDescription agent : result) {
                return agent.getName();
            }
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
        
        return null;
	}

	@Override
	protected void onTick() {
		int generatedNoise = rnd.nextInt(11);
		
		if (generatedNoise <= ((Student)myAgent).getNoise()) {
			System.out.println(myAgent.getLocalName() + " ::: noise ::: " + generatedNoise);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			
			msg.setOntology("NOISE");
			msg.addReceiver(floorSecurity);
			
			myAgent.send(msg);
			
			stop();
		}
	}
	
}
