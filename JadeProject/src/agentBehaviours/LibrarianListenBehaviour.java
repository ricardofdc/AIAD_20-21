package agentBehaviours;

import agents.Librarian;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

//FIPA Request Responder
public class LibrarianListenBehaviour extends AchieveREResponder {
    private Librarian librarian;


    public LibrarianListenBehaviour(Librarian librarian, MessageTemplate mt) {
        super(librarian, mt);
        this.librarian = librarian;
    }

    protected ACLMessage handleRequest(ACLMessage request) {
        ACLMessage reply = request.createReply();
        switch (request.getOntology()){
            case "TABLE":
                //agree
                reply.setPerformative(ACLMessage.AGREE);
                reply.setContent("TABLE request accepted");
                System.out.println("TABLE request accepted");
                registerPrepareResultNotification(new LibrarianRequestBehaviour(librarian, request));
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
        System.out.println(request);
        //registerPrepareResultNotification(new LibrarianRequestBehaviour(librarian, request));
        System.out.println("here");
        return result;
    }
}
