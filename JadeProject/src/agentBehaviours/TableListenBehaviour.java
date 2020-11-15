package agentBehaviours;

import agents.Table;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import library.Logs;

//FIPA Request Responder
public class TableListenBehaviour extends CyclicBehaviour {
    private final Table table;
    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

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
                default:
                    break;
            }
            myAgent.send(reply);
            Logs.write(myAgent.getName() + " SENT REPLY " + reply, "table", table.getFloor().getfloorNr());

        } else {
            block();
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
            		reply.setContent("YOU ARE SEATED");
            		
            		table.setIsFree(false);
            	} else {
            		reply.setPerformative(ACLMessage.REFUSE);
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
