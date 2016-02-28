package flowt.models.result;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.NoResultException;

import java.util.ArrayList;
import java.util.List;

public class ResultStatusFeature extends Feature {

    public List<IdRef<Result>> getResultsFor(Integer score) {

        List<IdRef<Result>> resultsForScore = new ArrayList<>();

        List<Result> results = all();
        for (Result result : results) {
            if (result.contains(score)) {
                resultsForScore.add(result.getId());
            }
        }
        return resultsForScore;
    }

    private List<Result> all() {
        return fetchOrCreateAll();
    }

    private List<Result> fetchOrCreateAll() {
        List<Result> results = new ArrayList<>();
        for (Status status : Status.values()) {
            Result result;
            IdRef<Result> id = id(Result.class, status.name().toLowerCase());
            try {
                result = id.fetch();
            } catch (NoResultException e) {
                result = new Result();
                result.id = id;
                result.max = status.defaultMax();
                result.min = status.defaultMin();
                yawp.save(result);
            }
            results.add(result);
        }


        return results;
    }
}
