package flowt.models.grade;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/grades")
public class Grade {

    @Id
    IdRef<Grade> id;

    public Integer count = 0;

    public IdRef<Grade> getId() {
        return id;
    }

    public Integer getCount() {
        return count;
    }
}
