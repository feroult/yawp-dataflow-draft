package flowt.models.work;

import flowt.models.student.StudentMarker;
import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;

@Endpoint(path = "/works")
public class Work {

    @Id
    IdRef<Work> id;

    @Index
    String index;

    @Json
    StudentMarker studentMarker;

    Boolean present;

    public Work() {

    }

    public Work(String index, StudentMarker studentMarker, boolean present) {
        this.index = index;
        this.studentMarker = studentMarker;
        this.present = present;
    }

    public StudentMarker getStudentMarker() {
        return studentMarker;
    }

    public boolean isPresent() {
        return present;
    }

    public IdRef<Work> getId() {
        return id;
    }
}
