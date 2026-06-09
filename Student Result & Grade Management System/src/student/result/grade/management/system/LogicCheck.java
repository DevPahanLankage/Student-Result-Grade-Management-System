package student.result.grade.management.system;

import student.result.grade.management.system.model.Result;
import student.result.grade.management.system.repository.SampleDataStore;
import student.result.grade.management.system.service.ResultService;
import student.result.grade.management.system.service.ValidationException;

public class LogicCheck {
    public static void main(String[] args) {
        SampleDataStore store = new SampleDataStore();
        ResultService service = new ResultService(store);
        int enrollmentId = store.getEnrollments().get(0).getId();

        checkGrade(service.recordResult(enrollmentId, 39, 39, 39), "F");
        checkGrade(service.recordResult(enrollmentId, 40, 40, 40), "D");
        checkGrade(service.recordResult(enrollmentId, 50, 50, 50), "C");
        checkGrade(service.recordResult(enrollmentId, 65, 65, 65), "B");
        checkGrade(service.recordResult(enrollmentId, 80, 80, 80), "A");

        try {
            service.recordResult(enrollmentId, 101, 50, 50);
            throw new IllegalStateException("Invalid mark was accepted.");
        } catch (ValidationException expected) {
        }

        System.out.println("Logic checks passed.");
    }

    private static void checkGrade(Result result, String expected) {
        if (!result.getGradeLetter().equals(expected)) {
            throw new IllegalStateException("Expected " + expected + " but got " + result.getGradeLetter());
        }
    }
}
