package org.example.listener;

import org.example.db.TestResultRepository;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.LocalDateTime;

public class TestResultListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        TestResultRepository.init();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        save(result, "PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        save(result, "FAILED");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        save(result, "SKIPPED");
    }

    private void save(ITestResult result, String status) {
        String name = result.getMethod().getMethodName();
        Object[] params = result.getParameters();
        if (params.length > 0) name = name + "[" + params[0] + "]";
        TestResultRepository.saveResult(name, status, LocalDateTime.now());
    }
}
