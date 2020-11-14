package agentBehaviours;

import agents.Security;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

//FIPA Request Responder
public class SecurityListenBehaviour extends AchieveREResponder {
    private Security security;
    public SecurityListenBehaviour(Security security, MessageTemplate mt) {
        super(security, mt);
        this.security = security;

    }

    protected ACLMessage handleRequest(ACLMessage request) {
        ACLMessage reply = request.createReply();
        switch (request.getOntology()){
            case "TABLE":
                //agree
                reply.setPerformative(ACLMessage.AGREE);
                reply.setContent("TABLE request accepted");
                //security.addBehaviour(new LibrarianRequestBehaviour(security, request));
                break;
            case "BOOK":
                //refuse
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("BOOK request refused");
                break;
            default:
                //refuse
                break;
        }
        return reply;
    }

    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
        ACLMessage result = request.createReply();
        result.setPerformative(ACLMessage.INFORM);

        return result;
    }
}
