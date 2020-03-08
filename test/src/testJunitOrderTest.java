import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.JVM)
public class testJunitOrderTest {

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @Test
    public void b_test_1(){
        System.out.println("b_test_1");
    }

    @Test
    public void r_test_2(){
        System.out.println("r_test_2");
    }

    @Test
    public void z_test_3(){
        System.out.println("z_test_3");
    }

    @Test
    public void l_test_4(){
        System.out.println("l_test_4");
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }
}