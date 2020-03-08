public class ThisEscape {
    public Integer i = 47;

    public ThisEscape(ClassA source){
        source.registerListener(
                new ClassAListener(){
                    public void onEvent(ClassAEvent e){
                        doSomething(e);
                        System.out.println("Value of i is:"+ThisEscape.this.i);
                    }
                }
        );
    }

    public ThisEscape(){
        new ClassA(){
            @Override
            public void registerListener(ClassAListener classAListener) {
                super.registerListener(classAListener);
                System.out.println(ThisEscape.this.i);
            }
        };

    }


    private void doSomething(ClassAEvent e) {
        System.out.println("Do something invoked.");
    }
}
