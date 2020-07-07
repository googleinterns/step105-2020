package com.google.sps.servlets;

import com.google.sps.data.SubtractionGame;
import com.google.sps.ApiExample;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/game")
public final class GameServlet extends HttpServlet {
  private ApiExample example = new ApiExample();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    String json = new Gson().toJson(example);
    response.getWriter().println(json);
     System.out.println("HELLLOOOOOOO^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^THER");
  }


}


