package flowt.models.score;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/scores")
public class Score {

    @Id
    IdRef<Score> id;

    Integer count = 0;

    public void inc() {
        count++;
    }

    public void dec() {
        count--;
    }
}
