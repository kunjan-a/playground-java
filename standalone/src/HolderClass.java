import static java.lang.System.out;

public class HolderClass {
//    private Charge holderField = new Charge();

    enum Charge {
        POSITIVE, NEGATIVE, NEUTRAL;

        Charge() {
            out.format("under construction%n");
        }
    }
}
