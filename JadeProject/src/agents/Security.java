package agents;

import agentBehaviours.ListeningBehaviour;
import agentBehaviours.WorkingBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import library.Floor;

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

    public void setup() {
        //addBehaviour(new WorkingBehaviour());
        //addBehaviour(new ListeningBehaviour(this));

        //System.out.println(getLocalName() + ": starting to work!");

        registerSecurity();
        getTablesAID();
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

                    for(int i = 0; i < result.length; i++) {
                        System.out.println("Security" + floor + " found " + result[i].getName());
                        tables.add(result[i].getName());
                    }
                } catch(FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });
    }



    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

}
