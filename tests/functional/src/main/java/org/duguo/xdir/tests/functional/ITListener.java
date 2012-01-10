package org.duguo.xdir.tests.functional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ITListener implements ITestListener,IConfigurationListener2, IMethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ITListener.class);

    public static void softSkippedTest(){
        LOG.info("soft skipped test: " + Reporter.getCurrentTestResult().getMethod().getTestClass().getName() + "." + Reporter.getCurrentTestResult().getMethod().getMethodName());
    }

    @Override
    public void beforeConfiguration(ITestResult tr) {
        LOG.info("executing: {}.{}", tr.getMethod().getTestClass().getName(), tr.getMethod().getMethodName());
    }

    @Override
    public void onConfigurationSuccess(ITestResult itr) {
    }

    @Override
    public void onConfigurationFailure(ITestResult itr) {
        if(itr.getStatus()== ITestResult.FAILURE && AbstractUIIT._webDriver!=null){
            LOG.error("page source for last failure test:\n{}",AbstractUIIT._webDriver.getPageSource());
        }
    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("executing: {}.{}", result.getMethod().getTestClass().getName(), result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if(result.getStatus()== ITestResult.FAILURE && AbstractUIIT._webDriver!=null){
            LOG.error("page source for last failure test:\n{}",AbstractUIIT._webDriver.getPageSource());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
    }



    @Override
    public List<IMethodInstance> intercept(List<org.testng.IMethodInstance> methods, org.testng.ITestContext context) {
        String excludedGroups = System.getenv("test.excluded.groups");
        if (excludedGroups != null) {
            return filterExcludedGroupForTestMethods(methods, excludedGroups);
        } else {
            return methods;
        }
    }

    private List<IMethodInstance> filterExcludedGroupForTestMethods(List<IMethodInstance> methods, String excludedGroups) {
        Set<String> excludes = convertGroupsStringToSet(excludedGroups);
        List<IMethodInstance> filteredMethods = new ArrayList<IMethodInstance>();
        for (IMethodInstance method : methods) {
            boolean skip = false;
            for (String group : method.getMethod().getGroups()) {
                if (excludes.contains(group)) {
                    skip = true;
                    LOG.debug("skipped test group " + group + ": " + method.getMethod().getTestClass().getName() + "." + method.getMethod().getMethodName());
                    break;
                }
            }
            if (!skip)
                filteredMethods.add(method);
        }
        return filteredMethods;
    }

    private Set<String> convertGroupsStringToSet(String excludedGroups) {
        Set<String> excludes = new HashSet<String>();
        for (String group : excludedGroups.split(",")) {
            excludes.add(group.trim());
        }
        return excludes;
    }
}
