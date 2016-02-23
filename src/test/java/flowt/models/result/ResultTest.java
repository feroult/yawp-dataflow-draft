package flowt.models.result;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import flowt.utils.EndpointTestCase;

public class ResultTest extends EndpointTestCase {

    @Test
    public void testCreate() {
        // TODO Auto-generated method stub
        String json=post("/results", "{}");
        Result result=from(json, Result.class);

        assertNotNull(result);
    }

}
