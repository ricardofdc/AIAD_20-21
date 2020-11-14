package agents;

import agentBehaviours.SecurityListenBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import library.Floor;
import library.Logs;

import java.util.ArrayList;

public class Security extends Agent {
    private final Floor floor;
    private final int noiseTolerance;

    private ArrayList<AID> tables;

    public Security(Floor floor, int noiseTolerance){
        this.floor=floor;
        this.noiseTolerance=noiseTolerance;
    }

    public Floor getFloor(){ return floor; }

    public int getNoiseTolerance() { return noiseTolerance; }

    public ArrayList<AID> getTables(){
        return tables;
    }

    public void setup() {
        registerSecurity();
        getTablesAID();
        addBehaviour(new SecurityListenBehaviour(this));
    }

    private void registerSecurity() {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("security");
        sd.setName(this.getName()); // name: security_1@192.168.1.91:1099/JADE
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            Logs.write(this.getName() + " REGISTERED AS SECURITY", "security");
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void getTablesAID() {
        int floor = this.floor.getfloorNr();
        addBehaviour(new WakerBehaviour(this, 1000) {
            @Override
            protected void onWake() {
                super.onWake();

                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();

                sd.setType("table_" + floor);
                dfd.addServices(sd);

                try {
                    DFAgentDescription[] result = DFService.search(myAgent, dfd);
                    tables = new ArrayList<AID>();

                    for (DFAgentDescription agent : result) {
                        Logs.write(this.myAgent.getName() + " FOUND " + agent.getName(), "security");
                        tables.add(agent.getName());
                    }
                } catch(FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });
    }



    protected void takeDown() {
        try {
            DFService.deregister(this);
            Logs.write(this.getName() + " TAKEN DOWN AND UNREGISTERED FROM DFSERVICE", "security");
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }

}
