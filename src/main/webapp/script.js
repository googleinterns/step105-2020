
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
  let playlist = await spotifyApi.getPlaylist('4vHIKV7j4QcZwgzGQcZg1x');
  let trackList = playlist.tracks.items;
  // let data = {
  //   trackList: trackList
  // };
  console.log(trackList);
  // let response = await fetch('/game', { 
  //   headers: {
  //     'Accept': 'application/json',
  //     'Content-Type': 'application/json'
  //   },
  //   method: 'POST',
  //   body: JSON.stringify(data)
  // });
}

let url = "https://open.spotify.com/playlist/0vvXsWCC9xrXsKd4FyS8kM?si=0pUHoH8QSt-go4_x4SvGVA";


getAuthToken();