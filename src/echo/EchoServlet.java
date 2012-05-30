package echo;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
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
      resp.getWriter().println("currentUser.getAuthDomain() : " + currentUser.getAuthDomain());
      resp.getWriter().println("currentUser.getEmail() : " + currentUser.getEmail());
      resp.getWriter().println("currentUser.getFederatedIdentity() : " + currentUser.getFederatedIdentity());
      resp.getWriter().println("currentUser.getNickname() : " + currentUser.getNickname());
      resp.getWriter().println("currentUser.getUserId() : " + currentUser.getUserId());
      resp.getWriter().println("userIsAdmin : " + userIsAdmin);
    } else {
      String loginUrl = users.createLoginURL("/");
      resp.getWriter().println("loginUrl : " + loginUrl);
    }

    resp.getWriter().println("--------------------------------------");
    OAuthService oauth = OAuthServiceFactory.getOAuthService();
    try {
      User user = oauth.getCurrentUser("https://www.googleapis.com/auth/userinfo.email");
      resp.getWriter().println("oauth.getCurrentUser(scope) : " + user);
      resp.getWriter().println("oauth.getCurrentUser(scope).getEmail() : " + user.getEmail());
      resp.getWriter().println(
          "oauth.getCurrentUser(scope).getAuthDomain() : " + user.getAuthDomain());
      resp.getWriter().println(
          "oauth.getCurrentUser(scope).getFederatedIdentity() : " + user.getFederatedIdentity());
      resp.getWriter().println("oauth.getCurrentUser(scope).getNickname() : " + user.getNickname());
      resp.getWriter().println("oauth.getCurrentUser(scope).getUserId() : " + user.getUserId());
    } catch (OAuthRequestException e) {
      resp.getWriter().println("oauth.getCurrentUser : " + e);
    }

    resp.getWriter().println("--------------------------------------");
    try {
      User user = oauth.getCurrentUser();
      resp.getWriter().println("oauth.getCurrentUser() : " + user);
      resp.getWriter().println("oauth.getCurrentUser().getEmail() : " + user.getEmail());
      resp.getWriter().println("oauth.getCurrentUser().getAuthDomain() : " + user.getAuthDomain());
      resp.getWriter().println(
          "oauth.getCurrentUser().getFederatedIdentity() : " + user.getFederatedIdentity());
      resp.getWriter().println("oauth.getCurrentUser().getNickname() : " + user.getNickname());
      resp.getWriter().println("oauth.getCurrentUser().getUserId() : " + user.getUserId());
    } catch (OAuthRequestException e) {
      resp.getWriter().println("oauth.getCurrentUser : " + e);
    }

    resp.getWriter().println("--------------------------------------");
  }
}
