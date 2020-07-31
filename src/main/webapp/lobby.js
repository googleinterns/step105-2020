const APP_ID = "1024158";
const CLIENT_KEY = "d15fbbe1c77552dc5097";
const PUSHER_APPLICATION_NAME = "song-guessing-game";
const PUSHER_CHAT_CHANNEL_NAME = "start-game";

async function startGame() {
  await fetch("/round", {
    method: "GET"
  });
}

function redirectToGamePage() {
  window.location.href = 'game.html';
}

var pusher = new Pusher(CLIENT_KEY, {
  cluster: "us2",
});

var channel = pusher.subscribe(PUSHER_APPLICATION_NAME);
channel.bind(PUSHER_CHAT_CHANNEL_NAME, function (data) {
  redirectToGamePage();
});

// Fetches list of usernames, appends each username to html list.
function loadUsernames() {
  // Get current url.
  let url = window.location.href;
  let paramString = parseRoomId(url);

  fetch(`/room?roomId=${paramString}`).then(response => response.json()).then((users) => {
    const userList = document.getElementById('user-list');
    users.forEach((username) => {
      userList.appendChild(createUsernameElement(username));
    })
  });
}

// Appends text node to list node, returns list node.
function createUsernameElement(username) {
  var node = document.createElement("li");
  var textnode = document.createTextNode(username);
  node.appendChild(textnode);
  return node;
}

function parseRoomId(url) {
  // Split url at '?' and saves second half (url parameter).
  let paramString = url.split('?')[1]; 
  let queryString = new URLSearchParams(paramString);

  // Saves value into roomId (pair[0] would be key).
  let roomId = "";
  for (let pair of queryString.entries()) {
	  roomId = pair[1]; 
  }

  return roomId;
}

window.addEventListener('DOMContentLoaded', ()=>{
    // Displays url on lobby page.
    document.getElementById("url").innerHTML = "Share the link!:<br>" + (window.location.href);
});
