package lda.services.libs.utils;

public class ClassResolver {
    private static final int DELIMITER_CLASS = '$';

    public static Class<?> fromName(final String packageName) {
        try {
            return Class.forName(rewriteNamePackage(packageName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to resolve class from ClassResolver: " + packageName, e);
        }
    }

    private static String rewriteNamePackage(final String packageName) {
        int delim = packageName.indexOf(DELIMITER_CLASS);
        if (delim == -1) {
            return packageName;
        }
        return packageName.substring(0, delim) + packageName
                .substring(delim + 1, packageName.indexOf(DELIMITER_CLASS, delim + 1));
//        String regex = "\\$([^$]+)\\$.*";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(packageName);
//        if (matcher.find()) {
//            return packageName.replace(matcher.group(), matcher.group(1));
//        }

    }
}
