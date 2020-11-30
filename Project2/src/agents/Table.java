package agents;

import agentBehaviours.TableListenBehaviour;
import sajas.core.Agent;
import sajas.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import library.Floor;
import library.Logs;

public class Table extends Agent {
    private final Floor floor;
    private boolean isFree;
    private int satisfaction;

    public Table(Floor floor){
        this.floor=floor;
        this.isFree = true;
        this.satisfaction = 0;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(int satisfaction) {
        this.satisfaction = satisfaction;
    }

    public Floor getFloor(){ return floor; }

    public boolean isFree(){
        return isFree;
    }
    
    public void setIsFree(boolean isFree) {
    	this.isFree = isFree;
    }

    public void setup() {
        registerTable();
        addBehaviour(new TableListenBehaviour(this));
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
            Logs.write(this.getName() + " REGISTERED AS TABLE_" + this.floor.getfloorNr(), "table", floor.getfloorNr());
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
    }

    protected void takeDown() {
        super.takeDown();
        try {
            DFService.deregister(this);
            Logs.write(this.getName() + " TAKEN DOWN AND UNREGISTERED FROM DFSERVICE", "table", floor.getfloorNr());
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }

}
