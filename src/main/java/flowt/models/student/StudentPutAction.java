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

public class StudentPutAction extends Action<Student> {

    @POST
    public Student put(Student student) {
        changeStudent(student);
        return student;
    }

    private void changeStudent(Student student) {
        yawp.begin();

        Student oldStudent = fetchOldStudent(student);
        yawp.save(student);

        StudentMarker marker = createMarker(student);
        yawp.save(marker);

        enqueueTasks(student, oldStudent);

        yawp.commit();
    }

    private void enqueueTasks(Student student, Student oldStudent) {
        if (hasChangedGrade(student, oldStudent)) {
            if (student.gradeId != null) {
                addStudentTask(student);
            }
            if (oldStudent != null && oldStudent.gradeId != null) {
                removeStudentTask(oldStudent);
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

    private void addStudentTask(Student student) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/api/grades/add-student").payload(to(student)));
    }

    private void removeStudentTask(Student student) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/api/grades/remove-student").payload(to(student)));
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
