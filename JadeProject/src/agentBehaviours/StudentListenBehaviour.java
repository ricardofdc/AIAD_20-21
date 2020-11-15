package agentBehaviours;

import java.util.ArrayList;

import agents.Security;
import agents.Student;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import library.Logs;

public class StudentListenBehaviour extends CyclicBehaviour {
	MessageTemplate mt = MessageTemplate.or(
            MessageTemplate.or( MessageTemplate.or( MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                                                    MessageTemplate.MatchPerformative(ACLMessage.INFORM)),
                                MessageTemplate.or( MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                                                    MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM))),
            MessageTemplate.or( MessageTemplate.MatchPerformative(ACLMessage.REFUSE),
                                MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD)));

    boolean isSeated = false;
    
    @Override
    public void action() {

        ACLMessage msg = myAgent.receive(mt);
        if(msg != null) {
            switch (msg.getPerformative()){
                case ACLMessage.AGREE:
                    Logs.write(myAgent.getName() + " RECEIVED AGREE FROM " + msg.getSender(), "student");
                    break;
                case ACLMessage.REFUSE:
                    handleRefuse();
                    Logs.write(myAgent.getName() + " RECEIVED REFUSE FROM " + msg.getSender(), "student");
                    break;
                case ACLMessage.NOT_UNDERSTOOD:
                    Logs.write(myAgent.getName() + " RECEIVED NOT_UNDERSTOOD FROM " + msg.getSender(), "student");
                    break;
                case ACLMessage.INFORM:
                	handleInform(msg);
                    Logs.write(myAgent.getName() + " RECEIVED INFORM FROM " + msg.getSender(), "student");
                    break;
                case ACLMessage.CONFIRM:
                	handleConfirm(msg);
                	Logs.write(myAgent.getName() + " RECEIVED CONFIRM FROM " + msg.getSender(), "student");
                	break;
                case ACLMessage.DISCONFIRM:
                    Logs.write(myAgent.getName() + " RECEIVED DISCONFIRM FROM " + msg.getSender(), "student");
                    break;
            }

        } else {
            block();
        }
    }

    private void handleConfirm(ACLMessage msg) {
    	if (isSeated)
    		return;
    	
    	ACLMessage reply = msg.createReply();
    	reply.setPerformative(ACLMessage.REQUEST);
    	reply.setOntology("SEAT");
    	
    	myAgent.send(reply);
    	
    	isSeated = true;
	}

	private void handleRefuse() {
        myAgent.addBehaviour(new WakerBehaviour(myAgent, 500) {
            @Override
            protected void onWake() {
                super.onWake();
                myAgent.addBehaviour(new StudentRequestBehaviour());
            }
        });
    }
    
    private void handleInform(ACLMessage msg) {
    	switch(msg.getOntology()) {
    	case "BEST_FLOOR":
    		
    		ACLMessage toSend = new ACLMessage(ACLMessage.REQUEST);
    		toSend.setOntology("TABLE");
    		
    		addTableReceivers(toSend.getContent(), toSend);
    		myAgent.send(toSend);
    		
    		break;
    	default:
    		break;
    	}
    }


    private void addTableReceivers(String floorNr, ACLMessage msg) {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();

        sd.setType("table_" + floorNr);
        dfd.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(myAgent, dfd);

            for (DFAgentDescription agent : result) {
                //Logs.write(myAgent.getName() + " FOUND " + agent.getName(), "student", floorNr);
                msg.addReceiver(agent.getName());
            }
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }

    }
}
