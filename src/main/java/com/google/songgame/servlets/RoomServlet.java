package com.google.songgame.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import javax.servlet.http.Cookie;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hc.core5.http.ParseException;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

@WebServlet("/room")
public final class RoomServlet extends HttpServlet {

  private static final Type MESSAGE_TYPE = new TypeToken<Map<String, String>>() {}.getType();
  private Gson gson;
  DatastoreService datastore;

  @Override
  public void init() {
    gson = new Gson();
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String> roomProperties = readJSONFromRequest(request);

    List<String> userIdList = new ArrayList<String>();
    userIdList.add(roomProperties.get("userId"));

    // Save roomId to datastore.
    Entity roomEntity = new Entity("Room");
    roomEntity.setProperty("roomId", roomProperties.get("roomId"));
    roomEntity.setProperty("userIdList", userIdList);

    datastore.put(roomEntity);
  }

  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String> roomProperties = readJSONFromRequest(request);

    Entity currentRoom = loadRoom(roomProperties.get("roomId"));

    // Add current user to existing datastore list.
    List<String> userIdList = (ArrayList) currentRoom.getProperty("userIdList");
    userIdList.add(roomProperties.get("userId"));

    currentRoom.setProperty("userIdList", userIdList);
    datastore.put(currentRoom);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String roomId = request.getParameter("roomId");

    Entity currentRoom = loadRoom(roomId);

    List<String> userIdList = (ArrayList) currentRoom.getProperty("userIdList");

    // Get each userId in the userIdList.
    Query userQuery = new Query("User").addFilter("userId", FilterOperator.IN, userIdList);
    PreparedQuery results = datastore.prepare(userQuery);

    List<String> usernameList = new ArrayList<String>();
    for (Entity currentUser : results.asIterable()) {
      String username = (String) currentUser.getProperty("username");
      usernameList.add(username);
    }

    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(usernameList));
  }

  private Map<String, String> readJSONFromRequest(HttpServletRequest request) throws IOException {
    String requestJSONString = request.getReader().lines().collect(Collectors.joining());
    Map<String, String> jsonData = gson.fromJson(requestJSONString, MESSAGE_TYPE);
    String userId = getUserId(request);
    jsonData.put("userId", userId);
    return jsonData;
  }

  private String getUserId(HttpServletRequest request) throws IOException {
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

  private Entity loadRoom(String roomId) {
    // Room query that looks for correct room.
    Query roomQuery = new Query("Room").addFilter("roomId", FilterOperator.EQUAL, roomId);
    PreparedQuery result = datastore.prepare(roomQuery);
    Entity currentRoom = result.asSingleEntity();
    return currentRoom;
  }
}
