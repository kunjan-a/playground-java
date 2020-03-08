import com.google.common.base.Strings;

import java.util.*;
import java.util.regex.Matcher;

public class ContactRow {
    public String rawRow;
    public LinkedHashSet<String> numberList = new LinkedHashSet<>();
    public LinkedHashSet<String> nameAddressList = new LinkedHashSet<>();
    public Map<String, String> field2Value;

    public ContactRow(String row, String[] fields) throws IncompleteRowException {
        rawRow = row;
        populateValues(fields);
    }

    private void populateValues(String[] fields) throws IncompleteRowException {
        field2Value = new LinkedHashMap<>();
        for (String field : fields) {
            field2Value.put(field, "");
        }
        int fieldIndex = 0;
        boolean insideQuotes = false;
        String currField = "";
        for (int i = 0; i < rawRow.length(); i++) {
            char currChar = rawRow.charAt(i);
            switch (currChar) {
                case '"':
                    insideQuotes = !insideQuotes;
                    break;
                case ',':
                    if (insideQuotes) {
                        currField += ' ';
                    } else {
                        final String field = fields[fieldIndex];
                        currField = sanitizeFieldValue(field, currField);
                        field2Value.put(field, currField);
                        currField = "";
                        fieldIndex++;
                        insideQuotes = false;
                    }
                    break;
                default:
                    currField += currChar;
            }
            if (insideQuotes && i == rawRow.length() - 1) {
                throw new IncompleteRowException();
            }
        }
    }

    private String sanitizeFieldValue(String field, String value) {
        if (isNameAddressField(field)) {
            value = sanitizeNameAddress(value);
        } else {
            if (isPhoneNumberField(field)) {
                value = sanitizePhone(value);
            }
        }
        return value;
    }

    private boolean isPhoneNumberField(String field) {
        return field.contains("Phone") && field.contains("Value");
    }

    private boolean isNameAddressField(String field) {
        return field.contains("Name") || field.contains("Address");
    }

    private String sanitizePhone(String value) {
        final String[] numbers = value.split(Matcher.quoteReplacement(":::"));
        List<String> newNumbers = new ArrayList<>(numbers.length);
        for (final String number : numbers) {
            String newNumber = "";
            for (int i = 0; i < number.length(); i++) {
                final char c = number.charAt(i);
                if ((i == 0 && c == '+') || Character.isDigit(c)) {
                    newNumber += String.valueOf(c);
                }
            }
            if (newNumber.startsWith("+91") && newNumber.length() > 3) {
                newNumber = newNumber.substring(3);
            } else if (newNumber.startsWith("+1") && newNumber.length() > 2) {
                newNumber = newNumber.substring(2);
            } else if (newNumber.startsWith("91") && newNumber.length() > 2) {
                newNumber = newNumber.substring(2);
            } else if (newNumber.startsWith("07") || newNumber.startsWith("08") || newNumber.startsWith("09")) {
                if (newNumber.length() == 11) {
                    newNumber = newNumber.substring(1);
                }
            }

            if (newNumber.length() < 6) {
                newNumber = "";
            }

            if (!newNumber.isEmpty()) {
                if (numberList.add(newNumber)) {
                    newNumbers.add(newNumber);
                }
            }
        }
        return String.join(" ::: ", newNumbers);
    }

    private String sanitizeNameAddress(String value) {
        String newValue = "";
        final char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            String suffix = String.valueOf(c);
            if (isSeparatorPunctuation(c)) {
                suffix = " ";
            } else {
                if (i < chars.length - 1) {
                    final int currCharType = Character.getType(c);
                    final int nextCharType = Character.getType(chars[i + 1]);
                    if (isCamelCase(currCharType, nextCharType)) {
                        suffix += ' ';
                    }
                }
            }
            newValue += suffix;
        }
        final String[] split = newValue.split(" ");
        String returnValue = "";
        for (String part : split) {
            if (!Strings.isNullOrEmpty(part)) {
                if (nameAddressList.add(part.toLowerCase())) {
                    if (Strings.isNullOrEmpty(returnValue)) {
                        returnValue = part;
                    } else {
                        returnValue = String.join(" ", returnValue, part);
                    }
                }
            }
        }
        return returnValue;
    }

    private boolean isSeparatorPunctuation(char c) {
        return c == '.' || c == '-' || c == '_';
    }

    private boolean isCamelCase(int currCharType, int nextCharType) {
        if (currCharType == Character.LOWERCASE_LETTER) {
            return nextCharType == Character.UPPERCASE_LETTER || nextCharType == Character.DECIMAL_DIGIT_NUMBER;
        } else if (currCharType == Character.UPPERCASE_LETTER) {
            return nextCharType == Character.DECIMAL_DIGIT_NUMBER;
        } else if (currCharType == Character.DECIMAL_DIGIT_NUMBER) {
            return nextCharType == Character.UPPERCASE_LETTER;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ContactRow{" +
                "rawRow='" + rawRow + '\'' +
                ", numberList=" + numberList +
                ", nameAddressList=" + nameAddressList +
                ", field2Value=" + field2Value +
                '}';
    }

    public void merge(ContactRow duplicate) {
        duplicate.field2Value.forEach((field, value) -> {
            final String trimmedValue = value.trim();
            if (!Strings.isNullOrEmpty(trimmedValue)) {
                mergeField(field, trimmedValue);
            }
        });
    }

    private void mergeField(String field, String trimmedValue) {
        if (isNameAddressField(field)) {
            mergeNameAddressValue(field, trimmedValue);
        } else if (isPhoneNumberField(field)) {
            mergePhoneNumberValue(field, trimmedValue);
        } else if (isTypeField(field)) {
            mergeTypeField(field, trimmedValue);
        } else {
            mergeFieldByConcat(field, trimmedValue, " ");
        }
    }

    private boolean isTypeField(String field) {
        return field.endsWith("Type");
    }

    private void mergeTypeField(String field, String toBeMergedValue) {
        field2Value.compute(field, (__, currValue) -> {
            if (Strings.isNullOrEmpty(currValue)) {
                return toBeMergedValue;
            } else {
                return currValue;
            }
        });

    }

    private void mergePhoneNumberValue(String field, String trimmedValue) {
        final String[] numbers = trimmedValue.split(":::");
        for (String number : numbers) {
            String trimmedNumber = number.trim();
            if (numberList.add(trimmedNumber)) {
                final String delimiter = " ::: ";
                mergeFieldByConcat(field, trimmedNumber, delimiter);
            }
        }
    }

    private void mergeNameAddressValue(String field, String trimmedValue) {
        final String[] strings = trimmedValue.split(" ");
        for (String nameAddressPart : strings) {
            if (nameAddressList.add(nameAddressPart.toLowerCase())) {
                final String delimiter = " ";
                mergeFieldByConcat(field, nameAddressPart, delimiter);
            }
        }
    }

    private void mergeFieldByConcat(String field, String toBeMergedValue, String delimiter) {
        field2Value.compute(field, (__, currValue) -> {
            if (Strings.isNullOrEmpty(currValue)) {
                return toBeMergedValue;
            } else {
                return String.join(delimiter, currValue, toBeMergedValue);
            }
        });
    }

    public String getFormattedString() {
        String row = "";
        for (String value : field2Value.values()) {
            if (Strings.isNullOrEmpty(row)) {
                if (Strings.isNullOrEmpty(value)) {
                    row = ",";
                } else {
                    row = value;
                }
            } else {
                row = String.join(",", row, value);
            }
        }
        return row;
    }

    public static class IncompleteRowException extends Exception {
    }
}
