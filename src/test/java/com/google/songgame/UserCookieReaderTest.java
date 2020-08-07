package com.google.songgame.data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import javax.servlet.http.Cookie;

@RunWith(JUnit4.class)
public class UserCookieReaderTest {

  @Test
  public void simpleTest() {
    Cookie c = new Cookie("userId", "_h1gyv6l8y");
    Cookie[] cookies = {c};
    String userId = UserCookieReader.getUserId(cookies);
    Assert.assertEquals("_h1gyv6l8y", userId);
  }

  @Test
  public void multipleCookies() {
    Cookie c0 = new Cookie("userId", "_h1gyv6l8y");
    Cookie c1 = new Cookie("youtubeFavorite", "gluggernaut");
    Cookie c2 = new Cookie("step-group", "105");
    Cookie[] cookies = {c0, c1, c2};
    String userId = UserCookieReader.getUserId(cookies);
    Assert.assertEquals("_h1gyv6l8y", userId);
  }

  @Test
  public void multipleCookiesUserIdInMiddle() {
    Cookie c0 = new Cookie("youtubeFavorite", "gluggernaut");
    Cookie c1 = new Cookie("userId", "_h1gyv6l8y");
    Cookie c2 = new Cookie("step-group", "105");
    Cookie[] cookies = {c0, c1, c2};
    String userId = UserCookieReader.getUserId(cookies);
    Assert.assertEquals("_h1gyv6l8y", userId);
  }

  @Test
  public void emptyCookieArray() {
    Cookie[] cookies = {};
    String userId = UserCookieReader.getUserId(cookies);
    Assert.assertEquals("", userId);
  }

  @Test
  public void cookieArrayWithNoUserId() {
    Cookie c0 = new Cookie("youtubeFavorite", "gluggernaut");
    Cookie c1 = new Cookie("step-group", "105");
    Cookie[] cookies = {c0, c1};
    String userId = UserCookieReader.getUserId(cookies);
    Assert.assertEquals("", userId);
  }
}
