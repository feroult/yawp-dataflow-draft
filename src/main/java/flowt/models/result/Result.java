package flowt.models.result;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/results")
public class Result {

    @Id
    IdRef<Result> id;

    Integer max;

    Integer min;

    Integer count = 0;

    public IdRef<Result> getId() {
        return id;
    }

    public void add(Integer count) {
        this.count += count;
    }

    public void remove(Integer count) {
        this.count -= count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        return id != null ? id.equals(result.id) : result.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public boolean contains(Integer score) {
        return score <= max && score >= min;
    }

    public boolean isRangeDifferent(Result oldResult) {
        return max != oldResult.max || min != oldResult.min;
    }
}
