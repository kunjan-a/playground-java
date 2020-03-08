public class TestBase {
    public TestBase() {
        this(45);
        System.out.println("Inside base first");
    }

    public TestBase(int i) {
        System.out.println("Inside base second, i:"+i);
        myMethod();
    }

    public void myMethod() {
        System.out.println("myMthod from base");
    }
}
