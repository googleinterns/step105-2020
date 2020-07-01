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
  private Pusher pusher;
  private Gson gson;

  @Override
  public void init() {
    pusher = new Pusher(APP_ID, CLIENT_KEY, CLIENT_SECRET);
    pusher.setCluster("us2");
    pusher.setEncrypted(true);
    gson = new Gson();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map dataForChatClients = readJSONFromRequest(request);
    pusher.trigger("spotify-game-app", "chat-update", dataForChatClients);
    sendResponseToClient(response, "complete");
    return;
  }

  public Map readJSONFromRequest(HttpServletRequest request) throws IOException {
    String requestJSONString = request.getReader().lines().collect(Collectors.joining());
    Map jsonData = gson.fromJson(requestJSONString, Map.class);
    return jsonData;
  }

  public void sendResponseToClient(HttpServletResponse response, String message) throws IOException {
    response.setContentType("application/json");
    String responseJsonString = gson.toJson(Collections.singletonMap("message", message));
    response.getWriter().println(responseJsonString);
  }

}
