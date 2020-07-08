// async function playTrackFromPlaylist() {
//   let previewURL = await getRandomPreviewURL();
//   let audio = document.getElementById("audio");
//   document.getElementById("song-url").src = previewURL;
//   audio.load();
//   audio.play();
// }

// async function getRandomPreviewURL() {
//   let playlist = await getPlaylist();
//   let trackList = playlist.tracks.items;
//   var randomTrack = trackList[Math.floor(Math.random() * trackList.length)];
//   var previewURL = randomTrack.track.preview_url;
//   return previewURL;
// }

// function getPlaylistID() {
//   let playlistID = getIdFromURL(document.getElementById("playlist-id").value);
//   return playlistID;
// }



// function getIdFromURL(url) {
//   var start = url.lastIndexOf('/') + 1;
//   var end = url.indexOf('?', start);
//   return url.slice(start, end);
// }

function embedPlaylist(){
fetch ('/game').then(response => response.json()).then((playlist) => {
  const ytPlayer = document.getElementById("ytplayer");
  ytPlayer.innerHTMl = playlist.items;
});
}