package flowt.models.result;

import io.yawp.testing.EndpointTestCaseBase;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ResultTest extends EndpointTestCaseBase {

    @Test
    public void testCreate() {
        // TODO Auto-generated method stub
        String json = post("/results", "{}");
        Result result = from(json, Result.class);

        assertNotNull(result);
    }

}
