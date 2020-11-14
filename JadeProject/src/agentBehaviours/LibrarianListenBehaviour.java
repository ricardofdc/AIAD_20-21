package agentBehaviours;

import agents.Librarian;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

//FIPA Request Responder
public class LibrarianListenBehaviour extends CyclicBehaviour {
    private final Librarian librarian;
    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    public LibrarianListenBehaviour(Librarian librarian){
        this.librarian = librarian;
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
                            //agree
                            reply.setPerformative(ACLMessage.AGREE);
                            reply.setContent("TABLE request accepted");
                            sendRequestToSecurity(msg);
                            break;
                        case "BOOK":
                            //refuse
                            reply.setPerformative(ACLMessage.REFUSE);
                            reply.setContent("BOOK request refused");
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

    private void sendRequestToSecurity(ACLMessage request){
        request.clearAllReceiver();
        for(int i=0; i<librarian.getFloorsSecurity().size(); i++){
            request.addReceiver(librarian.getFloorsSecurity().get(i));
        }
        request.setSender(myAgent.getAID());
        myAgent.send(request);
    }
}
