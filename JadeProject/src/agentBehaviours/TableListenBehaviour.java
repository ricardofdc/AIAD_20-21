package agentBehaviours;

import agents.Table;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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
                    switch (msg.getOntology()){
                        case "TABLE":
                            if(table.isFree()){
                                reply.setPerformative(ACLMessage.CONFIRM);
                                reply.setContent("free");
                            }
                            else {
                                reply.setPerformative(ACLMessage.DISCONFIRM);
                                reply.setContent("notFree");
                            }

                            break;
                        default:
                            //refuse
                            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                            break;
                    }
                    break;
                default:
                    break;
            }
            System.out.println(reply);
            myAgent.send(reply);

        } else {
            block();
        }
    }
}
