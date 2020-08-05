package com.google.songgame.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.util.List;

public final class RoomLoader {
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final int MAX_USERS = 20;

  public static Entity getRoom(String roomId) {
    Filter roomIdFilter = new FilterPredicate("roomId", FilterOperator.EQUAL, roomId);
    Query roomQuery = new Query("Room").setFilter(roomIdFilter);
    PreparedQuery result = datastore.prepare(roomQuery);
    Entity currentRoom = result.asSingleEntity();
    return currentRoom;
  }

  public static Entity getCurrentGameFromRoom(String roomId) {
    Filter roomIdFilter = new FilterPredicate("roomId", FilterOperator.EQUAL, roomId);
    Query gameQuery = new Query("Game").setFilter(roomIdFilter);
    PreparedQuery result = datastore.prepare(gameQuery);
    Entity currentGame = result.asSingleEntity();
    return currentGame;
  }

  public static List<Entity> getUsersInRoom(Entity room) {
    List<String> userIds = (List<String>) room.getProperty("userIdList");
    Filter usersInRoomFilter = new FilterPredicate("userId", FilterOperator.IN, userIds);
    Query usersInRoomQuery = new Query("User").setFilter(usersInRoomFilter);
    PreparedQuery result = datastore.prepare(usersInRoomQuery);
    return result.asList(FetchOptions.Builder.withLimit(MAX_USERS));
  }
}
