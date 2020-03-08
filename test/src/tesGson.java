import com.google.myjson.Gson;
import com.google.myjson.reflect.TypeToken;

import java.lang.reflect.Type;

public class tesGson {
    public static void main(String[] args) {
        final Gson gson = new Gson();
        final TestMe testMe = new TestMe();
        final String s = gson.toJson(testMe);
        System.out.println("String respresentation of json object 'TestMe' is:\n"+s);

        TestMe testMe2 = parseResponseJson(s);
        System.out.println("After gson deserialization: testMe2:"+testMe2.toString());

        Void testMe3 = parseResponseJsonVoid("{\"asdas\":\"assadassa\"}");
        System.out.println("After gson deserialization: testMe3:"+testMe3.toString());
    }

    private static TestMe parseResponseJson(String s) {
        Gson gson = new Gson();
        Type t = new TypeToken<TestMe>(){
        }.getType();
        return gson.fromJson(s,t);
    }

    private static Void parseResponseJsonVoid(String s) {
        Gson gson = new Gson();
        Type t = new TypeToken<Void>(){
        }.getType();
        return gson.fromJson(s,t);
    }

    public static class TestMe {
        private static String sVar = "svava";
        private static final String sfVar = "sfvava";
        private String var = "vava";
        public static String spVar = "spvava";
        public static final String spfVar = "spfvava";
        public transient String tvar = "tvava";

        @Override
        public String toString() {
            return "TestMe{" +
                    "var='" + var + '\'' +
                    ", tvar='" + tvar + '\'' +
                    '}';
        }
    }
}
