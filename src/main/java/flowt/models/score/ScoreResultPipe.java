package flowt.models.score;

import flowt.models.result.Result;
import flowt.models.result.ResultStatusFeature;
import io.yawp.repository.IdRef;
import io.yawp.repository.pipes.Pipe;
import io.yawp.repository.query.NoResultException;

import java.util.ArrayList;
import java.util.List;

public class ScoreResultPipe extends Pipe<Score, Result> {

    @Override
    public void configureSinks(Score score) {
        addSinkIds(feature(ResultStatusFeature.class).getResultsFor(getScoreValue(score)));
    }

    @Override
    public void configureSources(Result result) {
        List<Score> scores = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            if (!result.contains(i)) {
                continue;
            }

            IdRef<Score> id = id(Score.class, new Long(i));
            try {
                Score score = id.fetch();
                scores.add(score);
            } catch (NoResultException e) {
            }
        }
        addSources(scores);
    }

    @Override
    public void flux(Score score, Result result) {
        result.add(score.count);
    }

    @Override
    public void reflux(Score score, Result result) {
        result.remove(score.count);
    }

    @Override
    public boolean reflowCondition(Result newResult, Result oldResult) {
        return newResult.isRangeDifferent(oldResult);
    }

    private int getScoreValue(Score score) {
        return score.id.getId().intValue();
    }
}
