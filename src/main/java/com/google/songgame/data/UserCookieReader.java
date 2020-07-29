import javax.servlet.http.HttpServletRequest;

public final class UserCookieReader {
  public String getUserId(HttpServletRequest request) throws IOException {
    Cookie[] cookies = request.getCookies();
    String userId = "";
    for (Cookie cookie : cookies) {
      String name = cookie.getName();
      if (name.equals("userId")) {
        userId = cookie.getValue();
      }
    }

    if (userId.equals("")) {
      throw new Exception("ERROR: UserID cookie could not be found.");
    }

    return userId;
  }
}