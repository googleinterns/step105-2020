package com.google.songgame.data;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.servlet.http.Cookie;

/** Helper class to read data from Users Cookies */
public final class UserCookieReader {
  /** Helper method that reads and returns the user id stored in a cookie */
  public static String getUserId(Cookie[] cookies) {
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
