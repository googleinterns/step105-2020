// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.data.SubtractionGame;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import org.apache.hc.core5.http.ParseException;
import com.google.gson.Gson;
import java.util.ArrayList;

 

/** Servlet that encapsulates the subtraction game. */
@WebServlet("/game")
public final class GameServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String authToken = getAuthToken();
    ArrayList<String> jsonArray = new ArrayList<String>();
    jsonArray.add(authToken);
    Gson gson = new Gson();
    String jsonData = gson.toJson(jsonArray);
    response.setContentType("application/json;");
    response.getWriter().println(jsonData);

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println(request.getParameter("trackList"));
    // String playlistLink = request.getParameter("playlist-link");
    // System.out.println(playlistLink);
    // response.sendRedirect("/index.html");
  }

  private String getAuthToken() {
    String clientId = "1c29ff191b444611a6d9dbb4a354642f";
    String clientSecret = "6b92b6eb8fe24d5e98178407d3099bd8";
    SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).build();
    ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

    try {
      ClientCredentials clientCredentials = clientCredentialsRequest.execute();
      return clientCredentials.getAccessToken();
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
    return "";
  }

  // private void getSpotifyPlaylist(){
    
  //   String playlistId = "3AGOiaoRXMSjswCLtuNqv5";

    



  //   // GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(playlistId).build();
  //   GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(playlistId).build();
  //   try {
  //     PlaylistTrack tracks[] = getPlaylistsItemsRequest.execute().getItems();
  //     for (PlaylistTrack track : tracks) {
  //       System.out.println(track.getTrack().toString());
  //     }

  //   } catch (IOException | SpotifyWebApiException | ParseException e) {
  //     System.out.println("Error: " + e.getMessage());
  //   }  
  // }

}
