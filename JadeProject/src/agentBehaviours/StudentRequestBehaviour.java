package agentBehaviours;

import java.util.Vector;

import agents.Librarian;
import agents.Student;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import library.Logs;

//FIPA Request Initiator
public class StudentRequestBehaviour extends OneShotBehaviour {
	private final Student student;

	public StudentRequestBehaviour(Student a) {
		this.student = a;
	}

	@Override
	public void action() {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

		request.clearAllReceiver();
		for(int i=0; i<student.getLibrarianAID().size(); i++){
			request.addReceiver(student.getLibrarianAID().get(i));
		}
		request.setSender(student.getAID());
		switch (student.getAction()) {
			case 0 -> request.setOntology("TABLE");
			case 1 -> request.setOntology("BOOK");
		}
		request.setContent(student.getCourse());
		this.student.send(request);
	}
}
