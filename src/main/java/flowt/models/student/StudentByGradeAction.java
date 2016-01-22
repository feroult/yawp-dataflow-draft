package flowt.models.student;

import flowt.models.grade.Grade;
import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentByGradeAction extends Action<Student> {

    @GET
    public Map<IdRef<Grade>, Long> byGrade() {
        List<Student> students = yawp(Student.class).list();

        Map<IdRef<Grade>, Long> count = new HashMap<>();


        for (Student student : students) {

            IdRef<Grade> gradeId = student.gradeId;

            Long gradeCount = count.get(gradeId);

            if (gradeCount == null) {
                count.put(gradeId, 1L);
            } else {
                count.put(gradeId, ++gradeCount);
            }
        }

        return count;
    }

}
