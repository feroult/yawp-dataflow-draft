package flowt.models.student;

import flowt.models.score.Score;
import io.yawp.repository.pipes.Pipe;

public class StudentScorePipe extends Pipe<Student, Score> {

    @Override
    public void configureSinks(Student student) {
        if (student.score == null) {
            return;
        }
        addSinkId(id(Score.class, student.score.longValue()));
    }

    @Override
    public void flux(Student student, Score score) {
        score.inc();
    }

    @Override
    public void reflux(Student student, Score score) {
        score.dec();
    }
}
