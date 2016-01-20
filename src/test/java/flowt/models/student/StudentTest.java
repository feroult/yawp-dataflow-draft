package flowt.models.student;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import flowt.utils.EndpointTestCase;

public class StudentTest extends EndpointTestCase {

    @Test
    public void testCreate() {
        // TODO Auto-generated method stub
        String json=post("/students", "{}");
        Student student=from(json, Student.class);

        assertNotNull(student);
    }

}
