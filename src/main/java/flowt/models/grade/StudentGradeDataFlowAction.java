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

import java.util.List;

public class StudentGradeDataFlowAction extends Action<Grade> {

    public static final long POW_2_16 = (long) Math.pow(2, 16);

    public static final long POW_2_15 = (long) Math.pow(2, 15);

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

        long writers = memcache.increment(lock, 1, POW_2_16);

        if (writers < POW_2_16) {
            System.out.println("lock?!");
            memcache.increment(lock, -1);
            return false;
        }

        String indexHash = String.format("%s-%s", id, hash("" + index));
        Work work = new Work(indexHash, studentMarker, present);
        yawp.save(work);

        long now = System.currentTimeMillis();

        try {

            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            Queue queue = QueueFactory.getDefaultQueue();

            String taskName = String.format("%s-%d-%d", id, now / 1000 / 30, index).replaceAll("/", "__");

            System.out.println("adding: " + indexHash);
            queue.add(TaskOptions.Builder.withUrl("/api/grades/count-student-join").payload(joinPaylod(id, index, indexHash, lock))
                    .taskName(taskName).etaMillis(now + 1000));

        } catch (TaskAlreadyExistsException e) {

        } finally {
            memcache.increment(lock, -1);
        }

        return true;
    }

    @POST
    public void countStudentJoin(JoinPayload payload) {
        System.out.println("join: " + payload.indexHash);

        MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

        memcache.increment("index-" + payload.id, 1);
        memcache.increment(payload.lock, -1 * POW_2_15);

        for (int i = 0; i < 20; i++) {
            Long counter = (long) memcache.get(payload.lock);
            if (counter == null || counter < POW_2_15) {
                break;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        List<Work> works = yawp(Work.class).where("index", "=", payload.indexHash).order("id").list();

        boolean changed = false;
        yawp.begin();

        Grade grade = fetchGrade(payload.id);

        System.out.println("here");

        for (Work work : works) {
            System.out.println("working: " + work.getStudentMarker().getStudentId() + " - present: " + work.isPresent());
            if (countStudentIfLastVersion(payload.id, work.getStudentMarker(), work.isPresent(), grade)) {
                changed = true;
            }
        }

        if (!changed) {
            yawp.rollback();
        } else {
            if (grade.count == 0) {
                yawp.destroy(grade.id);
            } else {
                yawp.save(grade);
            }
            yawp.commit();
        }

        // delete
        for (Work work : works) {
            yawp.destroy(work.getId());
        }

    }

    private String joinPaylod(IdRef<Grade> id, Integer index, String indexHash, String lock) {
        return to(new JoinPayload(id, index, indexHash, lock));
    }

    private boolean countStudentIfLastVersion(IdRef<Grade> id, StudentMarker studentMarker, boolean present, Grade grade) {
        GradeStudentMarker gradeStudentMarker = fetchGradeStudentMarker(createGradeStudentMarkerId(id, studentMarker));

        if (gradeStudentMarker.version >= studentMarker.getVersion()) {
            yawp.rollback();
            return false;
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
        return true;
    }

    private void countStudentInGrade(IdRef<Grade> id, StudentMarker studentMarker, boolean present) {
        yawp.begin();
        Grade grade = fetchGrade(id);

        if (!countStudentIfLastVersion(id, studentMarker, present, grade)) {
            yawp.rollback();
            return;
        }

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
