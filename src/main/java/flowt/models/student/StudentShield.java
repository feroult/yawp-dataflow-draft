package flowt.models.student;

import io.yawp.repository.shields.Shield;

public class StudentShield extends Shield<Student> {

    @Override
    public void defaults() {
        // TODO Auto-generated method stub
        allow();
    }

}
