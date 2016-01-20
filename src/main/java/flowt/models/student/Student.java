package flowt.models.student;

import flowt.models.grade.Grade;
import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;

@Endpoint(path = "/students")
public class Student {

    @Id
    IdRef<Student> id;

    @Index
    IdRef<Grade> gradeId;

    public IdRef<Student> getId() {
        return id;
    }

    public IdRef<Grade> getGradeId() {
        return gradeId;
    }
}
