package flowt.servlets;

import io.yawp.repository.tools.DeleteAll;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DeleteAllServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        execute(resp.getWriter());
    }

    private void execute(PrintWriter writer) {
        DeleteAll.now();
        writer.println("ok");
    }

}
