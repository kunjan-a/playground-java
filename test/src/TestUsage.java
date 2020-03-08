import tst.Sib1;
import tst.Sib2;
import tst.Sib3;
import tst.TestParent;

import java.lang.reflect.Constructor;

public class TestUsage {

    public static void main(String[] s){
        final Class<Sib1> sib1 = TestParent.getSib1();
        final Sib1 gh = new Sib1("gh");

        final Class<Sib2> sib2 = TestParent.getSib2();
        final Sib2 sib21 = new Sib2();

        final Class<Sib3> sib3 = TestParent.getSib3();
        try {
            final Constructor<Sib3> constructor = sib3.getConstructor();

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
