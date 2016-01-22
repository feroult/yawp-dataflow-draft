package flowt.models.grade;

import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;

import java.util.List;

public class GradeTotalAction extends Action<Grade> {

    @GET
    public Long total() {
        long total = 0;

        List<Grade> grades = yawp(Grade.class).list();
        for (Grade grade : grades) {
            total += grade.count;
        }
        return total;
    }

}
