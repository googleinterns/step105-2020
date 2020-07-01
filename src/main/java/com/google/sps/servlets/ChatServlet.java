package com.google.sps.servlets;

import com.google.gson.Gson;
import com.pusher.rest.Pusher;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
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

    String requestJSONString = request.getReader().lines().collect(Collectors.joining());

    Gson gson = new Gson();
    Map pusherData = gson.fromJson(requestJSONString, Map.class);
    Pusher pusher = new Pusher("1024158", "d15fbbe1c77552dc5097", "91fd789bf568ec43d2ee");
    pusher.setCluster("us2");
    pusher.setEncrypted(true);
    pusher.trigger("spotify-game-app", "chat-update", pusherData);
    response.setContentType("application/json");
    String responseJsonString = gson.toJson(Collections.singletonMap("message", "complete"));
    response.getWriter().println(responseJsonString);
    return;
  }
}
