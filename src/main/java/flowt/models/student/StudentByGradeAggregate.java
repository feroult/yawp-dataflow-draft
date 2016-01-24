package flowt.models.student;

import flowt.models.grade.Grade;
import io.yawp.repository.IdRef;

public class StudentByGradeAggregate extends Aggregate<Student> {

    public IdRef<Grade> to(Student student) {
        return student.gradeId;
    }

    public void as() {
        count().as("count");
        sum("cost").as("cost");
        sum("hours").as("hours");
        avg("cost").as("avgCost");
    }

    // other ideas

    public void withWhere() {
        count().where("status", "=", "ativo").as("countAtivos");
        sum("cost").where("status", "=", "ativo").as("costAtivos");
    }

    public IdRef<Grade> reference(Student student) {
        return student.gradeId;
    }

    public Class<?> to() {
        return StudentsByGrade.class;
    }

    public void flow(Student source, Grade sink) {
    }

    private StudentByGradeAggregate avg(String cost) {
        return this;
    }

    private StudentByGradeAggregate sum(String cost) {
        return this;
    }


    private StudentByGradeAggregate where(String status, String s, String ativo) {
        return this;
    }

    private StudentByGradeAggregate count() {
        return this;
    }

    private void as(String maxCost) {

    }

    private StudentByGradeAggregate max(String cost) {
        return this;
    }

    private void count(String count) {
    }

}

