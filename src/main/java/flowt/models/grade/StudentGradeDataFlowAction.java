package flowt.models.grade;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import flowt.models.student.StudentMarker;
import flowt.models.work.Work;
import io.yawp.commons.http.annotation.POST;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;
import io.yawp.repository.query.NoResultException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class StudentGradeDataFlowAction extends Action<Grade> {

    public static final double POW_2_16 = Math.pow(2, 16);

    @POST
    public void addStudent(IdRef<Grade> id, StudentMarker studentMarker) {
        System.out.println(String.format("add student [%s] to [%s]", studentMarker.getStudentId(), id));
        fork(id, studentMarker, true);
    }

    @POST
    public void removeStudent(IdRef<Grade> id, StudentMarker studentMarker) {
        System.out.println(String.format("remove student [%s] from [%s]", studentMarker.getStudentId(), id));
        fork(id, studentMarker, false);
    }

    private boolean fork(IdRef<Grade> id, StudentMarker studentMarker, boolean present) {
        MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
        Integer index = (Integer) memcache.get("index-" + id);
        if (index == null) {
            memcache.put("index-" + id, 1);
            index = (Integer) memcache.get("index-" + id);
        }

        String lock = String.format("%s-lock-%d", id, index);

        long writers = memcache.increment(lock, 1, (long) POW_2_16);

        if (writers < POW_2_16) {
            System.out.println("lock?!");
            memcache.increment(lock, -1);
            return false;
        }

        String indexHash = hash("" + index);
        Work work = new Work(String.format("%s-%s", id, indexHash), studentMarker, present);
        yawp.save(work);

        long now = System.currentTimeMillis();

        try {

            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            Queue queue = QueueFactory.getDefaultQueue();

            String taskName = String.format("%s-%d-%d", id, now / 1000 / 30, index).replaceAll("/", "__");

            System.out.println("adding: " + indexHash);
            queue.add(TaskOptions.Builder.withUrl("/api/grades/count-student-join").payload(indexHash)
                    .taskName(taskName).etaMillis(now + 1000));

        } catch (TaskAlreadyExistsException e) {

        } finally {
            memcache.increment(lock, -1);
        }

        return true;
    }

    @POST
    public void countStudentJoin(String indexHash) {
        System.out.println("join: " + indexHash);
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

    private String hash(String index) {
        byte[] shaArray = DigestUtils.sha(index);
        byte[] encodedArray = new Base64().encode(shaArray);
        String returnValue = new String(encodedArray);
        returnValue = StringUtils.removeEnd(returnValue, "\r\n");
        return returnValue.replaceAll("=", "").replaceAll("/", "-").replaceAll("\\+", "\\_");
    }


}
