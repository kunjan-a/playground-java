public class TestChild extends TestBase {
    public TestChild() {
        //super(34);
        System.out.println("test child first");
    }

    public TestChild(int i) {
        System.out.println("test child second with i:"+i);
        myMethod();
    }

    @Override
    public void myMethod() {
        System.out.println("myMethod from child");
    }
}
