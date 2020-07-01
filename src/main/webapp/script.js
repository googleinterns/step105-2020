async function playTrackFromPlaylist() {
  let previewURL = await getRandomPreviewURL();
  document.getElementById("song-url").src = previewURL;
  audio.load();
  audio.play();
}

async function getRandomPreviewURL() {
  let playlist = await getPlaylist();
  let trackList = playlist.tracks.items;
  var randomTrack = trackList[Math.floor(Math.random() * trackList.length)];
  var previewURL = randomTrack.track.preview_url;
  return previewURL;
}

async function getPlaylist() {
  let authToken = await getAuthToken();
  let spotifyApi = new SpotifyWebApi();
  spotifyApi.setAccessToken(authToken);
  let playlistID = getIdFromURL(document.getElementById("playlist-id").value);
  let playlist = await spotifyApi.getPlaylist(playlistID);
  return playlist;
}

async function getAuthToken() {
  let authTokenRequest = await fetch('/game', {method: 'GET'});
  let authTokenResponse = await authTokenRequest.json();
  let authToken = authTokenResponse[0];
  return authToken;
}

function getIdFromURL(url) {
  if (url.search('open.spotify.com/playlist/')) {
    var start = url.lastIndexOf('/') + 1;
    if (url.search('?')) {
      var end = url.indexOf('?', start);  
    } else {
      var end = str.length + 1;  
    } 
    return url.slice(start, end);
  } else {
    throw "Must be a playlist link from Spotify."
  }
}

// Testing exports
exports.getIdFromURL = getIdFromURL;
