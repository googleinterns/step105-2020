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

  private final static Type MESSAGE_TYPE = new TypeToken<Map<String, String>>(){}.getType();
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

    // Save roomId to datastore.
    Entity roomEntity = new Entity("Room");
    roomEntity.setProperty("roomId", roomProperties.get("roomId"));
    roomEntity.setProperty("userId", roomProperties.get("userId"));
    
    datastore.put(roomEntity);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Room");
    PreparedQuery results = datastore.prepare(query);
    
    Map<String, String[]> roomIdParamMap = request.getParameterMap();

    for (String roomId : roomIdParamMap.keySet()) {
    String[] idArray = (String[]) roomIdParamMap.get(roomId);
      for (String val : idArray) {
        for (Entity entity : results.asIterable()) {
          String roomIdValue = (String) entity.getProperty("roomId");
          val = roomIdValue;
        }
      }
    }

    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(roomIdParamMap));
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

}
