
function getIdFromURL(url) {
  var start = url.lastIndexOf('/') + 1;
  var end = url.indexOf('?', start);
  return url.slice(start, end);
}

async function getAuthToken() {
  let authTokenRequest = await fetch('/game', {method: 'GET'});
  let authToken = await authTokenRequest.json();
  let myToken = authToken[0];
  let spotifyApi = new SpotifyWebApi();
  spotifyApi.setAccessToken(myToken);
  let playlistID = getIdFromURL(document.getElementById("playlist-id").value);
  console.log(playlistID);
  let playlist = await spotifyApi.getPlaylist(playlistID);
  let trackList = playlist.tracks.items;
  var previewURL = trackList[Math.floor(Math.random() * trackList.length)].track.preview_url;
  console.log(previewURL);
  document.getElementById("song-url").src = previewURL;
  audio.load();
  audio.play();
  console.log(trackList);

}

let url = "https://open.spotify.com/playlist/0vvXsWCC9xrXsKd4FyS8kM?si=0pUHoH8QSt-go4_x4SvGVA";


getAuthToken();

