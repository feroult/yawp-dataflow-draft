package flowt.models.grade;

import io.yawp.repository.IdRef;

public class JoinPayload {
    IdRef<Grade> id;

    Integer index;

    String indexHash;

    public JoinPayload(IdRef<Grade> id, Integer index, String indexHash) {
        this.id = id;
        this.index = index;
        this.indexHash = indexHash;
    }
}
