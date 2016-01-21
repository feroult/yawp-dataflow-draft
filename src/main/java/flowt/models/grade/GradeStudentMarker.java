package flowt.models.grade;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;

@Endpoint(path = "/grade-student-marker")
public class GradeStudentMarker {

    @Id
    IdRef<GradeStudentMarker> id;

    @ParentId
    IdRef<Grade> gradeId;

    Integer version;

    Boolean present;

}
