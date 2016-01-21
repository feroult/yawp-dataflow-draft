package flowt.models.student;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;

@Endpoint(path = "/student-marker")
public class StudentMarker {

    @Id
    IdRef<StudentMarker> id;

    @ParentId
    IdRef<Student> studentId;

    Integer version;

    public IdRef<Student> getStudentId() {
        return studentId;
    }

    public Integer getVersion() {
        return version;
    }
}
