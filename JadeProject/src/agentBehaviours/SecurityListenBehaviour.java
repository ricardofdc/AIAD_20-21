package agentBehaviours;

import agents.Security;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

//FIPA Request Responder
public class SecurityListenBehaviour extends CyclicBehaviour {
    private Security security;
    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST );
    private int numResponses = 0;
    private int numTables = 0;
    private String course = "";
    private AID librarianAID;

    public SecurityListenBehaviour(Security security) {
        this.security = security;

    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if(msg != null) {
            ACLMessage reply = msg.createReply();
            switch (msg.getPerformative()){
                case ACLMessage.REQUEST:        // librarian request
                    switch (msg.getOntology()){
                        case "TABLE":
                            //agree
                            if(numResponses != 0){
                                reply.setPerformative(ACLMessage.REFUSE);
                                reply.setContent("TABLE request refused");
                            }
                            else {
                                reply.setPerformative(ACLMessage.AGREE);
                                reply.setContent("TABLE request accepted");
                                this.course = msg.getContent();
                                this.librarianAID = msg.getSender();
                                getFreeTables();
                            }
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
                    myAgent.send(reply);
                    break;
                case ACLMessage.CONFIRM:        //tables response
                    numTables++;
                    numResponses++;
                    if(numResponses == security.getTables().size()){
                        replyLibrarian(getSecuritySatisfaction());
                        numTables = 0;
                        numResponses = 0;
                    }
                    break;
                case ACLMessage.DISCONFIRM:     //tables response
                    numResponses++;
                    if(numResponses == security.getTables().size()){
                        replyLibrarian(getSecuritySatisfaction());
                        numTables = 0;
                        numResponses = 0;
                    }
                    break;
                default:
                    break;
            }
        } else {
            block();
        }
    }

    void getFreeTables(){
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.clearAllReceiver();
        for(int i=0; i<security.getTables().size(); i++){
            request.addReceiver(security.getTables().get(i));
        }
        request.setOntology("TABLE");
        request.setContent("isFree?");
        request.setSender(myAgent.getAID());
        myAgent.send(request);
    }


    int getSecuritySatisfaction(){
        if(numTables == 0){
            return 0;
        }
        int satisfaction = 0;
        if(this.course.equals(this.security.getFloor().getCourse())){
            satisfaction += 50;
        }
        satisfaction += numTables * 50 / this.security.getTables().size();
        replyLibrarian(satisfaction);

        return satisfaction;

    }

    private void replyLibrarian(int satisfaction) {
        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
        reply.clearAllReceiver();
        reply.addReceiver(librarianAID);
        reply.setOntology("SATISFACTION");
        reply.setContent(String.valueOf(satisfaction));
        reply.setSender(myAgent.getAID());
        myAgent.send(reply);
    }
}
