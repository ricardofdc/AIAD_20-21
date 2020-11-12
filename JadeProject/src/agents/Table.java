package agents;

import agentBehaviours.ListeningBehaviour;
import agentBehaviours.WorkingBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import library.Floor;

public class Table extends Agent {
    private final Floor floor;
    private boolean isFree;


    public Table(Floor floor){
        this.floor=floor;
        this.isFree = true;
    }

    public Floor getFloor(){ return floor; }


    public boolean isFree(){
        return isFree;
    }

    public void setup() {
        //addBehaviour(new WorkingBehaviour());
        //addBehaviour(new ListeningBehaviour(this));

        //System.out.println(getLocalName() + ": starting to work!");

        registerTable();
    }

    private void registerTable() {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("table_" + this.floor.getfloorNr());
        sd.setName(this.getName()); // name: 008_barbara@192.168.1.91:1099/JADE
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

}
