import agents.Student;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class JADELauncher {

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();
        p1.setParameter(Profile.GUI, "true");
        ContainerController mainContainer = rt.createMainContainer(p1);

        AgentController ac1;
        try {
            ac1 = mainContainer.acceptNewAgent("person1", new Student("name1", "MIEIC", 2));
            ac1.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        AgentController ac2;
        try {
            ac2 = mainContainer.acceptNewAgent("person2", new Student("name2", "MIEIC", 3));
            ac2.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }

}
