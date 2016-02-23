package flowt.models.score;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import flowt.utils.EndpointTestCase;

public class ScoreTest extends EndpointTestCase {

    @Test
    public void testCreate() {
        // TODO Auto-generated method stub
        String json=post("/scores", "{}");
        Score score=from(json, Score.class);

        assertNotNull(score);
    }

}
