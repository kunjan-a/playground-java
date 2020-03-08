import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

public class VcfParserMain {
    public static void main(String[] args) throws FileNotFoundException {
        String filename = "/Users/kunjan_aggarwal/Desktop/pa mi/fromGoogle/17 july/google.csv";
        final GoogleCSVParser parser = new GoogleCSVParser();

        final List<ContactRow> parsed = parser.parse(filename);

        List<ContactRow> deduped = parser.dedupeByNumber(parsed);
        System.out.println(parsed.size() + " deduped to " + deduped.size());

        String outputFile = filename + "_" + new Date().getTime();
        parser.writeContacts(deduped, outputFile);
    }
}
