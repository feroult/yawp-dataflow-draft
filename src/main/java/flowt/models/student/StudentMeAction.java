package flowt.models.student;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.actions.Action;

import java.util.HashMap;
import java.util.Map;

public class StudentMeAction extends Action<Student> {

    @GET
    public Object me() {

        Map<String, String> map = new HashMap<>();

        UserService userService = UserServiceFactory.getUserService();

        map.put("mail", userService.getCurrentUser().getEmail());
        map.put("admin", userService.isUserAdmin() + "");

        return map;
    }

}
