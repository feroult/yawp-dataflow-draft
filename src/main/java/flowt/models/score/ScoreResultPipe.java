package flowt.models.score;

import flowt.models.result.Result;
import flowt.models.result.ResultStatusFeature;
import io.yawp.repository.IdRef;
import io.yawp.repository.pipes.Pipe;

public class ScoreResultPipe extends Pipe<Score, Result> {

    @Override
    public IdRef<Result> sinkId(Score score) {
        return feature(ResultStatusFeature.class).getResultFor(getScoreValue(score)).getId();
    }

    @Override
    public void flux(Score score, Result result) {
        result.add(score.count);
    }

    @Override
    public void reflux(Score score, Result result) {
        result.remove(score.count);
    }

    private int getScoreValue(Score score) {
        return score.id.getId().intValue();
    }
}
