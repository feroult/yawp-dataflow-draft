package flowt.models.grade;

import flowt.models.student.Student;
import io.yawp.commons.http.annotation.POST;
import io.yawp.repository.actions.Action;

public class GradeDataFlowStudentAction extends Action<Grade> {

    @POST
    public void addStudent(Student student) {
        System.out.println(String.format("add student [%s] to [%s]", student.getId(), student.getGradeId()));
    }

    @POST
    public void removeStudent(Student student) {
        System.out.println(String.format("remove student [%s] from [%s]", student.getId(), student.getGradeId()));
    }

}
