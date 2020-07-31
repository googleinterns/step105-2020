package com.google.songgame.data;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.servlet.http.Cookie;

public final class UserCookieReader {
  public static String getUserId(HttpServletRequest request) throws IOException {
    Cookie[] cookies = request.getCookies();
    String userId = "";
    for (Cookie cookie : cookies) {
      String name = cookie.getName();
      if (name.equals("userId")) {
        userId = cookie.getValue();
      }
    }

    if (userId.equals("")) {
      System.err.println("ERROR: UserID cookie could not be found.");
    }

    return userId;
  }
}
