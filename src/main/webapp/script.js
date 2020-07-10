
function embedPlaylist(){
fetch ('/game').then(response => response.json()).then((videoID) => {
  document.getElementById("player").src = "http://www.youtube.com/embed/" + videoID;
});
}