package flowt.models.grade;

import flowt.models.student.StudentMarker;
import io.yawp.repository.IdRef;

public class JoinPayload {

    IdRef<Grade> id;

    Integer index;

    String indexHash;

    String lock;

    public JoinPayload(IdRef<Grade> id, Integer index, String indexHash, String lock) {
        this.id = id;
        this.index = index;
        this.indexHash = indexHash;
        this.lock = lock;
    }


}
