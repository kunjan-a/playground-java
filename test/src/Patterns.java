import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use {@link #linkify(String)} to find URLs in a string
 */
class Patterns {

    /**
     * Good characters for Internationalized Resource Identifiers (IRI).
     * This comprises most common used Unicode characters allowed in IRI
     * as detailed in RFC 3987.
     * Specifically, those two byte Unicode characters are not included.
     */
    private static final String GOOD_IRI_CHAR =
            "a-zA-Z0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";

    /**
     * This only matched IPv4 addresses
     */
    private static final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\." +
                    "(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\." +
                    "(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\." +
                    "(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))");

    /**
     * RFC 1035 Section 2.3.4 limits the labels to a maximum 63 octets.
     */
    private static final String IRI
            = "[" + GOOD_IRI_CHAR + "]([" + GOOD_IRI_CHAR + "\\-]{0,61}[" + GOOD_IRI_CHAR + "]){0,1}";

    private static final String GOOD_GTLD_CHAR =
            "a-zA-Z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";
    private static final String GTLD = "[" + GOOD_GTLD_CHAR + "]{2,63}";
    private static final String HOST_NAME = "(" + IRI + "\\.)+" + GTLD;

    private static final Pattern DOMAIN_NAME
            = Pattern.compile("(" + HOST_NAME + "|" + IP_ADDRESS + ")");

    /**
     * Regular expression pattern to match most part of RFC 3987
     * Internationalized URLs, aka IRIs.  Commonly used Unicode characters are
     * added.
     */
    private static final Pattern WEB_URL = Pattern.compile(
            "((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                    + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                    + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
                    + "(?:" + DOMAIN_NAME + ")"
                    + "(?:\\:\\d{1,5})?)" // plus option port number
                    + "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
                    + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
                    + "(?:\\b|$)"); // and finally, a word boundary or end of
    // input.  This is to stop foo.sure from
    // matching as foo.su

    /**
     * Filters out web URL matches that occur after an at-sign (@).  This is
     * to prevent turning the domain name in an email address into a web link.
     */
    private static boolean notPartOfEmailAddress(CharSequence s, int start, int end) {
        return start == 0 || s.charAt(start - 1) != '@';
    }

    private static String makeUrl(String url) {
        boolean hasPrefix = false;
        final String[] prefixes = {"http://", "https://", "rtsp://"};
        for (String prefix : prefixes) {
            if (url.regionMatches(true, 0, prefix, 0,
                                  prefix.length())) {
                hasPrefix = true;

                // Fix capitalization if necessary
                if (!url.regionMatches(false, 0, prefix, 0,
                                       prefix.length())) {
                    url = prefix + url.substring(prefix.length());
                }

                break;
            }
        }

        if (!hasPrefix) {
            url = prefixes[0] + url;
        }

        return url;
    }


    /**
     * Sample input: "my name is domain.to and I am http://www.google.com/path/hun?ty=98 created by kunj@domain.to with web addr www.domain.to done resolving to 127.0.8.5."
     * <br/>Corresponding O/P:
     * <br/>Linkified 'domain.to' to point to 'http://domain.to'
     * <br/>Linkified 'http://www.google.com/path/hun?ty=98' to point to 'http://www.google.com/path/hun?ty=98'
     * <br/>Linkified 'www.domain.to' to point to 'http://www.domain.to'
     * <br/>Linkified '127.0.8.5' to point to 'http://127.0.8.5'
     */
    public static List<String> linkify(String s) {
        Matcher m = Patterns.WEB_URL.matcher(s);

        List<String> links = new ArrayList<>();
        while (m.find()) {
            int start = m.start();
            int end = m.end();

            if (notPartOfEmailAddress(s, start, end)) {
                String url = makeUrl(m.group(0));
                final String urlSource = s.substring(start, end);
                System.out.println("Linkified '" + urlSource + "' to point to '" + url + "'");
                links.add(url);
            }
        }
        return links;
    }
}
