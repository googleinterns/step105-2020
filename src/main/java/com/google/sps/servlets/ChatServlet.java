package com.google.sps.servlets;

import com.google.gson.Gson;
import com.pusher.rest.Pusher;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/chat")
public final class ChatServlet extends HttpServlet {

  final private String APP_ID = "1024158";
  final private String CLIENT_KEY = "d15fbbe1c77552dc5097";
  final private String CLIENT_SECRET = "91fd789bf568ec43d2ee";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // String chatInput = request.getParameter("message");
    // java.util.Map<java.lang.String, java.lang.String[]> vals = request.getParameterMap();
    // for (String name : vals.keySet()) System.out.println("key: " + name);

    String body = request.getReader().lines().collect(Collectors.joining());
    // System.out.println(chatInput);
    Gson gson = new Gson();
    System.out.println(body);
    Map toPusher = gson.fromJson(body, Map.class);
    Pusher pusher = new Pusher("1024158", "d15fbbe1c77552dc5097", "91fd789bf568ec43d2ee");
    pusher.setCluster("us2");
    pusher.setEncrypted(true);
    pusher.trigger("spotify-game-app", "chat-update", toPusher);
    // response.setContentType("application/json");
    // String jsonData = gson.toJson(data);
    // response.getWriter().println(jsonData);
    return;
  }
}
