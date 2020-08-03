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
import javax.servlet.http.Cookie;

@WebServlet("/user")
public final class UserServlet extends HttpServlet {

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
    Map<String, String> userProperties = readJSONFromRequest(request);

    // Save player username and userId to datastore.
    Entity userEntity = new Entity("User");
    userEntity.setProperty("username", userProperties.get("username"));
    userEntity.setProperty("userId", userProperties.get("userId"));

    datastore.put(userEntity);
  }

  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String> roomProperties = readJSONFromRequest(request);

    // Room query that looks for correct room.
    Query userQuery =
        new Query("User").addFilter("userId", FilterOperator.EQUAL, roomProperties.get("userId"));
    PreparedQuery result = datastore.prepare(userQuery);
    Entity currentUser = result.asSingleEntity();

    // Add current user to existing datastore list.
    currentUser.setProperty("roomId", roomProperties.get("roomId"));

    datastore.put(currentUser);
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
