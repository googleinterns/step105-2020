// imports
const authToken = require('/.authToken');
const playlistURL = require('./playlistURL')

async function playTrackFromPlaylist() {
  let previewURL = await playlistURL.getRandomPreviewURL();
  document.getElementById("song-url").src = previewURL;
  audio.load();
  audio.play();
}


async function getPlaylist() {
  let authToken = await authToken.getAuthToken();
  let spotifyApi = new SpotifyWebApi();
  spotifyApi.setAccessToken(authToken);
  let playlistID = playlistURL.getIdFromURL(document.getElementById("playlist-id").value);
  let playlist = await spotifyApi.getPlaylist(playlistID);
  return playlist;
}



