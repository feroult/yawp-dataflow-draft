package flowt.models.student;

import flowt.models.grade.Grade;
import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentByGradeAction extends Action<Student> {

    @GET
    public Map<IdRef<Grade>, Integer> byGrade() {
        return getCountsByGrade();
    }

    @GET
    public List<Map<String, Object>> verifyCounts() {
        Map<IdRef<Grade>, Integer> countsByGrade = getCountsByGrade();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Grade grade : yawp(Grade.class).list()) {
            Integer count = countsByGrade.get(grade.getId());

            if (count == null) {
                count = 0;
            }

            if (!count.equals(grade.getCount())) {
                Map<String, Object> diff = new HashMap<>();
                diff.put("gradeId", grade.getId());
                diff.put("countInGrade", grade.getCount());
                diff.put("realCount", count);
                result.add(diff);
            }

        }

        return result;
    }

    private Map<IdRef<Grade>, Integer> getCountsByGrade() {
        List<Student> students = yawp(Student.class).list();

        Map<IdRef<Grade>, Integer> count = new HashMap<>();


        for (Student student : students) {

            IdRef<Grade> gradeId = student.gradeId;

            Integer gradeCount = count.get(gradeId);

            if (gradeCount == null) {
                count.put(gradeId, 1);
            } else {
                count.put(gradeId, ++gradeCount);
            }
        }

        return count;
    }

}
