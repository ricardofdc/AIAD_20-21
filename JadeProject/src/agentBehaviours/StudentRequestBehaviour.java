package agentBehaviours;

import java.util.Vector;

import agents.Librarian;
import agents.Student;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import library.Logs;

//FIPA Request Initiator
public class StudentRequestBehaviour extends AchieveREInitiator {
	private final Student student;

	public StudentRequestBehaviour(Student a, ACLMessage msg) {
		super(a, msg);
		this.student = a;
	}

	protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
		Vector<ACLMessage> v = new Vector<ACLMessage>();
		msg.clearAllReceiver();
		for(int i=0; i<student.getLibrarianAID().size(); i++){
			msg.addReceiver(student.getLibrarianAID().get(i));
		}
		msg.setSender(student.getAID());
		switch (student.getAction()) {
			case 0 -> msg.setOntology("TABLE");
			case 1 -> msg.setOntology("BOOK");
		}
		msg.setContent(student.getCourse());
		v.add(msg);
		return v;
	}

	protected void handleAgree(ACLMessage agree) {
		Logs.write(student.getName() + " " + agree.getContent(), "student");
	}

	protected void handleRefuse(ACLMessage refuse) {
		Logs.write(student.getName() + " " + refuse.getContent(), "student");
	}

	protected void handleInform(ACLMessage inform) {
		// ...
	}

	protected void handleFailure(ACLMessage failure) {
		// ...
	}

}
