package echo;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class EchoServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(EchoServlet.class.getName());

  @Override
  public void service(HttpServletRequest request, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    @SuppressWarnings("unchecked")
    Enumeration<String> names = request.getHeaderNames();

    while (names.hasMoreElements()) {
      String name = names.nextElement();
      String value = request.getHeader(name);
      String msg = name + ": " + value;
      resp.getWriter().println(msg);
      logger.info(msg);
    }

    resp.getWriter().println("--------------------------------------");
    UserService users = UserServiceFactory.getUserService();
    boolean userLoggedIn = users.isUserLoggedIn();
    resp.getWriter().println("userLoggedIn: " + userLoggedIn);
    if (userLoggedIn) {
      User currentUser = users.getCurrentUser();
      boolean userIsAdmin = users.isUserAdmin();
      resp.getWriter().println("currentUser : " + currentUser);
      resp.getWriter().println("userIsAdmin : " + userIsAdmin);
    } else {
      String loginUrl = users.createLoginURL("/");
      resp.getWriter().println("loginUrl : " + loginUrl);
    }
  }
}
