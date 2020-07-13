package com.google.sps;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class GameServletTest {

  @Test
  public void testGetIDFromURL() {
    GameServlet gameServlet = new GameServlet();
    Assert.assertEquals("h", "h");
  }
}
