package flowt.models.grade;

import flowt.models.student.StudentMarker;
import io.yawp.commons.http.annotation.POST;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;
import io.yawp.repository.query.NoResultException;

public class StudentGradeDataFlowAction extends Action<Grade> {

    @POST
    public void addStudent(IdRef<Grade> id, StudentMarker studentMarker) {
        System.out.println(String.format("add student [%s] to [%s]", studentMarker.getStudentId(), id));
        countStudentInGrade(id, studentMarker, true);
    }

    @POST
    public void removeStudent(IdRef<Grade> id, StudentMarker studentMarker) {
        System.out.println(String.format("remove student [%s] from [%s]", studentMarker.getStudentId(), id));
        countStudentInGrade(id, studentMarker, false);
    }

    private void countStudentInGrade(IdRef<Grade> id, StudentMarker studentMarker, boolean present) {
        yawp.begin();
        Grade grade = fetchGrade(id);
        GradeStudentMarker gradeStudentMarker = fetchGradeStudentMarker(createGradeStudentMarkerId(id, studentMarker));

        if (gradeStudentMarker.version >= studentMarker.getVersion()) {
            yawp.rollback();
            return;
        }

        if (gradeStudentMarker.present) {
            grade.count--;
        }
        if (present) {
            grade.count++;
        }

        gradeStudentMarker.present = present;
        gradeStudentMarker.version = studentMarker.getVersion();

        yawp.save(gradeStudentMarker);

        if (grade.count == 0) {
            yawp.destroy(id);
        } else {
            yawp.save(grade);
        }

        yawp.commit();
    }

    private Grade fetchGrade(IdRef<Grade> id) {
        try {
            return id.fetch();
        } catch (NoResultException e) {
            Grade grade = new Grade();
            grade.id = id;
            return grade;
        }
    }

    private GradeStudentMarker fetchGradeStudentMarker(IdRef<GradeStudentMarker> gradeStudentMarkerId) {
        try {
            return gradeStudentMarkerId.fetch();
        } catch (NoResultException e) {
            GradeStudentMarker gradeStudentMarker = new GradeStudentMarker();
            gradeStudentMarker.id = gradeStudentMarkerId;
            gradeStudentMarker.gradeId = gradeStudentMarkerId.getParentId();
            gradeStudentMarker.version = 0;
            gradeStudentMarker.present = false;
            return gradeStudentMarker;
        }
    }

    private IdRef<GradeStudentMarker> createGradeStudentMarkerId(IdRef<Grade> id, StudentMarker studentMarker) {
        return id.createChildId(GradeStudentMarker.class, studentMarker.getStudentId().getId());
    }

}
