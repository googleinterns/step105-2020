package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hc.core5.http.ParseException;
import com.google.gson.Gson; 
import java.util.ArrayList;

@WebServlet("/room")
public final class RoomServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get user name
    String name = request.getParameter("user-name");
    response.setContentType("text/html;");
    response.getWriter().println("Name: " + name);
  }

}
