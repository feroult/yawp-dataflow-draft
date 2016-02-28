package flowt.models.score;

import io.yawp.testing.EndpointTestCaseBase;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ScoreTest extends EndpointTestCaseBase {

    @Test
    public void testCreate() {
        // TODO Auto-generated method stub
        String json = post("/scores", "{}");
        Score score = from(json, Score.class);

        assertNotNull(score);
    }

}
