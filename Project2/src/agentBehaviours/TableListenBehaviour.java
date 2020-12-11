package agentBehaviours;

import agents.Table;
import sajas.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import library.Logs;

public class TableListenBehaviour extends CyclicBehaviour {
    private final Table table;
    MessageTemplate mt = MessageTemplate.or(
    		MessageTemplate.MatchPerformative(ACLMessage.INFORM),
    		MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

    public TableListenBehaviour(Table table){
        this.table = table;
    }


    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if(msg != null) {
            ACLMessage reply = msg.createReply();
            switch (msg.getPerformative()){
                case ACLMessage.REQUEST:
                    Logs.write(myAgent.getName() + " RECEIVED REQUEST FROM " + msg.getSender(), "table", table.getFloor().getfloorNr());
                    reply = handleRequest(msg, reply);
                    break;
                case ACLMessage.INFORM:
                	Logs.write(myAgent.getName() + " RECEIVED INFORM FROM " + msg.getSender(), "table", table.getFloor().getfloorNr());
                	handleInform(msg);
                	break;
                default:
                    break;
            }
            myAgent.send(reply);
            Logs.write(myAgent.getName() + " SENT REPLY " + reply, "table", table.getFloor().getfloorNr());

        } else {
            block();
        }
    }

    private void handleInform(ACLMessage msg) {
		switch (msg.getOntology()) {
		case "SET_EMPTY":
			table.setIsFree(true);
			table.setSatisfaction(0);
			break;
		}
	}

	private ACLMessage handleRequest(ACLMessage request, ACLMessage reply) {
        switch (request.getOntology()){
            case "TABLE":
                if (table.isFree()) {
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent("free");
                }
                else {
                    reply.setPerformative(ACLMessage.DISCONFIRM);
                    reply.setContent("notFree");
                }

                break;
            case "SEAT":
            	if (table.isFree()) {
            		reply.setPerformative(ACLMessage.AGREE);
            		reply.setOntology("SEAT");
            		reply.setContent(this.table.getX() + " " + this.table.getY());
            		table.setIsFree(false);
            		String studentCourse = request.getContent();
                    if(studentCourse.equals(table.getFloor().getCourse())){
                        table.setSatisfaction(1);
                    }
                    else{
                        table.setSatisfaction(0);
                    }
            	} else {
            		reply.setPerformative(ACLMessage.REFUSE);
            		reply.setOntology("SEAT");
            		reply.setContent("SORRY BUT YOUR PLACE WAS TAKEN");
            	}
            	break;
            default:
                //refuse
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                break;
        }
        return reply;

    }
}
