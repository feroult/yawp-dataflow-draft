package flowt.models.student;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.commons.http.annotation.POST;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;
import io.yawp.repository.query.NoResultException;

import java.util.ConcurrentModificationException;

public class StudentPutAction extends Action<Student> {

    @POST
    public Student put(Student student) {
        int count = 1;
        while (!changeStudent(student)) {
            count++;
            if (count > 5) {
                System.out.println("max retries...");
                return null;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return student;
    }

    private boolean changeStudent(Student student) {

        try {
            yawp.begin();

            Student oldStudent = fetchOldStudent(student);
            yawp.save(student);

            StudentMarker marker = createMarker(student);
            yawp.save(marker);

            enqueueTasks(marker, student, oldStudent);

            yawp.commit();
            return true;

        } catch (ConcurrentModificationException e) {
            yawp.rollback();
        }
        return false;
    }

    private void enqueueTasks(StudentMarker marker, Student student, Student oldStudent) {
        if (hasChangedGrade(student, oldStudent)) {
            if (student.gradeId != null) {
                addStudentTask(student, marker);
            }
            if (oldStudent != null && oldStudent.gradeId != null) {
                removeStudentTask(oldStudent, marker);
            }
        }
    }

    private boolean hasChangedGrade(Student student, Student oldStudent) {
        if (oldStudent == null) {
            return true;
        }

        if (oldStudent.gradeId == null && student.gradeId == null) {
            return false;
        }

        if (oldStudent.gradeId == null || student.gradeId == null) {
            return true;
        }

        return !oldStudent.gradeId.equals(student.gradeId);
    }

    private Student fetchOldStudent(Student student) {
        try {
            if (student.id != null) {
                return student.id.fetch();
            }
        } catch (NoResultException e) {
        }
        return null;
    }

    private void addStudentTask(Student student, StudentMarker marker) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Queue queue = QueueFactory.getDefaultQueue();

        queue.add(TaskOptions.Builder.withUrl(actionUri(student, "add-student")).payload(to(marker)));
    }

    private String actionUri(Student student, String action) {
        return String.format("/api%s/%s", student.gradeId, action);
    }

    private void removeStudentTask(Student student, StudentMarker marker) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl(actionUri(student, "remove-student")).payload(to(marker)));
    }

    private StudentMarker createMarker(Student student) {
        IdRef<StudentMarker> markerId = student.id.createChildId(StudentMarker.class, 1L);
        StudentMarker marker;
        try {
            marker = markerId.fetch();
            marker.version++;
        } catch (NoResultException e) {
            marker = new StudentMarker();
            marker.id = markerId;
            marker.studentId = student.id;
            marker.version = 1;
        }
        return marker;
    }

}
