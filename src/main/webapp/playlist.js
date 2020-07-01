async function playTrackFromPlaylist() {
  let previewURL = await getRandomPreviewURL();
  document.getElementById("song-url").src = previewURL;
  audio.load();
  audio.play();
}


async function getPlaylist() {
  let authToken = await getAuthToken();
  let spotifyApi = new SpotifyWebApi();
  spotifyApi.setAccessToken(authToken);
  let playlistID = getIdFromURL(document.getElementById("playlist-id").value);
  let playlist = await spotifyApi.getPlaylist(playlistID);
  return playlist;
}



