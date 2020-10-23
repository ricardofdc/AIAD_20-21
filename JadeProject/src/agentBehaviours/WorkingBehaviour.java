package agentBehaviours;

import jade.core.behaviours.Behaviour;

public class WorkingBehaviour extends Behaviour {
    private int n = 0;

    public void action() {
        System.out.println(++n + " I am doing something!");
    }

    public boolean done() {
        return n == 3;
    }
}
