import com.google.myjson.annotations.SerializedName;

public class ClassA {
    @SerializedName("naam") private String _naamString;
    public void registerListener(ClassAListener classAListener) {
    }

    @Override
    public String toString() {
        return "ClassA{" +
                "_naamString='" + _naamString + '\'' +
                '}';
    }
}
