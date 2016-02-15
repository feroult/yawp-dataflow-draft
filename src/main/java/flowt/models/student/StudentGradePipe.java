package flowt.models.student;

import flowt.models.grade.Grade;
import io.yawp.repository.pipes.Pipe;

public class StudentGradePipe extends Pipe<Student, Grade> {
    @Override
    public void configure(Student student) {
        addSink(student.gradeId);
    }

    @Override
    public void clear(Grade grade) {
        grade.count = 0;
    }

    @Override
    public void flux(Student student, Grade grade) {
        grade.count++;
    }

    @Override
    public void reflux(Student student, Grade grade) {
        grade.count--;
    }
}
