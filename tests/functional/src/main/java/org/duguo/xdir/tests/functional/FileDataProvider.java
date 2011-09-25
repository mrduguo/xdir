package org.duguo.xdir.tests.functional;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.DataProvider;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;

public class FileDataProvider {
    @DataProvider(name = "getStringListFromFile")
    public static Iterator<Object[]> getStringListFromFile(Method testMethod) throws Exception {
        Map<String, String> arguments = FileDataProvider.resolveDataProviderArguments(testMethod);
        List<String> lines = FileDataProvider.getRawLinesFromResource(arguments.get("resourcePath"));
        List<Object[]> data = new ArrayList<Object[]>();
        for (String line : lines) {
            String normalizedLine = line.trim();
            if (!normalizedLine.startsWith("#")) {
                data.add(new Object[]{normalizedLine});
            }
        }
        return data.iterator();
    }

    @SuppressWarnings("unchecked")
    public static List<String> getRawLinesFromResource(String resourcePath) throws Exception {
        InputStream is = FileDataProvider.class.getResourceAsStream(resourcePath);
        List<String> lines = IOUtils.readLines(is, "UTF-8");
        is.close();
        return lines;
    }

    protected static Map<String, String> resolveDataProviderArguments(Method testMethod) throws Exception {
        if (testMethod == null)
            throw new IllegalArgumentException("Test Method context cannot be null.");

        DataProviderArguments args = testMethod.getAnnotation(DataProviderArguments.class);
        if (args == null)
            throw new IllegalArgumentException("Test Method context has no DataProviderArguments annotation.");
        if (args.value() == null || args.value().length == 0)
            throw new IllegalArgumentException("Test Method context has a malformed DataProviderArguments annotation.");
        Map<String, String> arguments = new HashMap<String, String>();
        for (int i = 0; i < args.value().length; i++) {
            String[] parts = args.value()[i].split("=");
            arguments.put(parts[0], parts[1]);
        }
        return arguments;
    }
}
