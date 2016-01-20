package flowt.models.grade;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import flowt.utils.EndpointTestCase;

public class GradeTest extends EndpointTestCase {

    @Test
    public void testCreate() {
        // TODO Auto-generated method stub
        String json=post("/grades", "{}");
        Grade grade=from(json, Grade.class);

        assertNotNull(grade);
    }

}
