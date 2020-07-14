package com.google.sps.servlets;

import com.google.gson.Gson;
import com.pusher.rest.Pusher;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Collections;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

@WebServlet("/chat")
public final class ChatServlet extends HttpServlet {

  private final static String APP_ID = "1024158";
  private final static String CLIENT_KEY = "d15fbbe1c77552dc5097";
  private final static String CLIENT_SECRET = "91fd789bf568ec43d2ee";
  private final static String PUSHER_APPLICATION_NAME = "song-guessing-game";
  private final static String PUSHER_CHAT_CHANNEL_NAME = "chat-update";
  private Pusher pusher;
  private Gson gson;

  // TODO: @salilnadkarni remove temp variables and integrate datastore
  Map status  = new HashMap<Integer,Boolean>();
  String ANSWER = "Google";

  @Override
  public void init() {
    pusher = new Pusher(APP_ID, CLIENT_KEY, CLIENT_SECRET);
    pusher.setCluster("us2");
    pusher.setEncrypted(true);
    gson = new Gson();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map dataFromChatClient = readJSONFromRequest(request);
    Map responseForPusherChat = createPusherChatResponse(dataFromChatClient);
    pusher.trigger(PUSHER_APPLICATION_NAME, PUSHER_CHAT_CHANNEL_NAME, responseForPusherChat);
    sendResponseToClient(response, "complete");
    return;
  }

  private Map readJSONFromRequest(HttpServletRequest request) throws IOException {
    String requestJSONString = request.getReader().lines().collect(Collectors.joining());
    Map jsonData = gson.fromJson(requestJSONString, Map.class);
    String userId = getUserId(request);
    jsonData.put("userId", userId);
    return jsonData;
  }

  private Map createPusherChatResponse(Map data) {
    Map response = new HashMap<String, String>();
    String userId = (String) data.get("userId");
    String message = (String) data.get("message");
    String messageType = "guess";
    if (checkStatus(userId)) {
      messageType = "spectator";
    } else if (checkGuess(message)) {
      updateStatus(userId);
      messageType = "correct";
      message = "guessed correctly!";
    }
    response.put("username", userId);
    response.put("message", message);
    response.put("messageType", messageType);
    return response;
  }

  private String getUserId(HttpServletRequest request) throws IOException {
    Cookie[] cookies = request.getCookies();
    String userId = "";
    final int USER_ID_INDEX = 0;
    try {
      userId = cookies[USER_ID_INDEX].getValue();
    } catch (Exception e) {
      System.err.println("ERROR: UserID cookie could not be found.");
    }
    return userId;
  }

  // TODO: @salilnadkarni update checkStatus / updateStatus to use Datastore
  private boolean checkStatus(String userId) {
    if (!status.containsKey(userId)) {
      status.put(userId, false);
    }
    return (Boolean) status.get(userId) == true;
  }

  private void updateStatus(String userId) {
    status.replace(userId, true);
  }

  // TODO: @salilnadkarni update checkGuess to use current video title
  private boolean checkGuess(String message) {
    return message.equals(ANSWER);
  }

  private void sendResponseToClient(HttpServletResponse response, String message) throws IOException {
    response.setContentType("application/json");
    String responseJsonString = gson.toJson(Collections.singletonMap("message", message));
    response.getWriter().println(responseJsonString);
  }

}
