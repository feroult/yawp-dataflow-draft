package flowt.models.result;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.NoResultException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResultStatusFeature extends Feature {

    public Result getResultFor(Integer score) {
        List<Result> results = all();
        for (Result result : results) {
            if (score >= result.threshold) {
                return result;
            }
        }
        return results.get(results.size() - 1);
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
                result.threshold = status.defaultThreshold();
                yawp.save(result);
            }
            results.add(result);
        }

        Collections.sort(results, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                return o2.threshold.compareTo(o1.threshold);
            }
        });

        return results;
    }
}
