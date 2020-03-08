import com.google.common.base.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class GoogleCSVParser {

    public List<ContactRow> dedupeByNumber(List<ContactRow> parsed) {
        LinkedHashMap<String, LinkedHashSet<ContactRow>> number2contactList = getContactsByNumber(parsed);

        Map<Integer, LinkedHashSet<ContactRow>> duplicateAsSet = getDuplicatesAsSet(number2contactList);

        return dedupe(duplicateAsSet);
    }

    private List<ContactRow> dedupe(Map<Integer, LinkedHashSet<ContactRow>> duplicateAsSet) {
        final ArrayList<ContactRow> mergedContacts = new ArrayList<>(duplicateAsSet.size());
        for (LinkedHashSet<ContactRow> duplicates : duplicateAsSet.values()) {
            ContactRow mergedRow = null;
            for (ContactRow duplicate : duplicates) {
                if (Objects.isNull(mergedRow)) {
                    mergedRow = duplicate;
                } else {
                    mergedRow.merge(duplicate);
                }
            }
            mergedContacts.add(mergedRow);
        }
        return mergedContacts;
    }

    private LinkedHashMap<String, LinkedHashSet<ContactRow>> getContactsByNumber(List<ContactRow> parsed) {
        LinkedHashMap<String, LinkedHashSet<ContactRow>> number2contactList = new LinkedHashMap<>();
        for (ContactRow contactRow : parsed) {
            for (String number : contactRow.numberList) {
                number2contactList.compute(number, (__, contactRows) -> {
                    if (contactRows == null) {
                        contactRows = new LinkedHashSet<>();
                    }
                    contactRows.add(contactRow);
                    return contactRows;
                });
            }
        }
        return number2contactList;
    }

    private Map<Integer, LinkedHashSet<ContactRow>> getDuplicatesAsSet(LinkedHashMap<String, LinkedHashSet<ContactRow>> number2contactList) {
        Map<ContactRow, Integer> contact2Identifier = new HashMap<>();
        Map<Integer, LinkedHashSet<ContactRow>> duplicateAsSet = new HashMap<>();
        Integer id = 0;
        for (LinkedHashSet<ContactRow> contactRows : number2contactList.values()) {
            Integer newId = id++;
            final LinkedHashSet<ContactRow> duplicateWithNewId = new LinkedHashSet<>();

            Set<Integer> oldIds = new HashSet<>();
            for (ContactRow contactRow : contactRows) {
                contact2Identifier.compute(contactRow, (__, oldId) -> {
                    if (Objects.isNull(oldId)) {
                        duplicateWithNewId.add(contactRow);
                    } else {
                        oldIds.add(oldId);
                    }
                    return newId;
                });
            }

            if (!oldIds.isEmpty()) {
                for (Integer oldId : oldIds) {
                    final LinkedHashSet<ContactRow> removed = duplicateAsSet.remove(oldId);
                    for (ContactRow contactRow : removed) {
                        contact2Identifier.put(contactRow, newId);
                        duplicateWithNewId.add(contactRow);
                    }
                }
            }

            duplicateAsSet.put(newId, duplicateWithNewId);
        }
        return duplicateAsSet;
    }

    public void writeContacts(List<ContactRow> deduped, String outputFile) throws FileNotFoundException {
        final File file = new File(outputFile);
        final PrintWriter printWriter = new PrintWriter(file);
        final ContactRow row1 = deduped.get(0);
        String firstRow = "";
        for (String fieldName : row1.field2Value.keySet()) {
            if (Strings.isNullOrEmpty(firstRow)) {
                firstRow = fieldName;
            } else {
                firstRow = String.join(",", firstRow, fieldName);
            }
        }
        printWriter.println(firstRow);
        for (ContactRow contactRow : deduped) {
            printWriter.println(contactRow.getFormattedString());
        }
    }

    public enum Field {
        Name, Given_Name, Additional_Name, Family_Name, Yomi_Name, Given_Name_Yomi, Additional_Name_Yomi,
        Family_Name_Yomi, Name_Prefix, Name_Suffix, Initials, Nickname, Short_Name, Maiden_Name, Birthday, Gender, Location,
        Billing_Information, Directory_Server, Mileage, Occupation, Hobby, Sensitivity, Priority, Subject, Notes, Language, Photo,
        Group_Membership, E_mail_1_Type, E_mail_1_Value, E_mail_2_Type, E_mail_2_Value, Phone_1_Type, Phone_1_Value,
        Phone_2_Type, Phone_2_Value, Phone_3_Type, Phone_3_Value, Phone_4_Type, Phone_4_Value, Phone_5_Type,
        Phone_5_Value, Address_1_Type, Address_1_Formatted, Address_1_Street, Address_1_City, Address_1_PO_Box,
        Address_1_Region, Address_1_Postal_Code, Address_1_Country, Address_1_Extended_Address, Organization_1_Type,
        Organization_1_Name, Organization_1_Yomi_Name, Organization_1_Title, Organization_1_Department,
        Organization_1_Symbol, Organization_1_Location, Organization_1_Job_Description
    }

    public List<ContactRow> parse(String filename) throws FileNotFoundException {
        final Scanner scanner = new Scanner(new File(filename));
        final String columns = scanner.nextLine();
        final String[] fields = columns.split(",");
        List<ContactRow> parsed = new ArrayList<>();
        while (scanner.hasNextLine()) {
            ContactRow contactRow = null;
            boolean anotherLineNeeded = false;
            String row = "";
            do {
                row += scanner.nextLine();
                if (anotherLineNeeded) {
                    System.out.println("Incomplete row changed to:" + row);
                    anotherLineNeeded = false;
                }
                try {
                    contactRow = new ContactRow(row, fields);
                } catch (ContactRow.IncompleteRowException e) {
                    System.out.println("incomplete row:" + row);
                    anotherLineNeeded = true;
                }
            } while (anotherLineNeeded);
            parsed.add(contactRow);
        }
        return parsed;
    }
}
