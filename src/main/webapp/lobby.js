const APP_ID = "1024158";
const CLIENT_KEY = "d15fbbe1c77552dc5097";
const PUSHER_APPLICATION_NAME = "song-guessing-game";
const PUSHER_GAME_CHANNEL_NAME = "start-game";

let url = window.location.href;
let roomId = parseRoomId(url);

window.addEventListener("DOMContentLoaded", () => {
  document.getElementById("start-game").addEventListener("click", startGame);
});

// Connect to Pusher
var pusher = new Pusher(CLIENT_KEY, {
  cluster: "us2",
});
var channel = pusher.subscribe(PUSHER_APPLICATION_NAME);

channel.bind(PUSHER_GAME_CHANNEL_NAME, function () {
  redirectToGamePage();
});

async function startGame() {
  data = {
    roomId: roomId,
  };

  await fetch("/game", {
    method: "POST",
    body: JSON.stringify(data),
  });
  
  await fetch("/round", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

function redirectToGamePage() {
  window.location.href = `game.html?roomId=${roomId}`;
}

// Fetches list of usernames, appends each username to html list.
function loadUsernames() {
  fetch(`/room?roomId=${roomId}`)
    .then((response) => response.json())
    .then((users) => {
      const userList = document.getElementById("user-list");
      users.forEach((username) => {
        userList.appendChild(createUsernameElement(username));
      });
    });
}

// Appends text node to list node, returns list node.
function createUsernameElement(username) {
  var node = document.createElement("li");
  var textnode = document.createTextNode(username);
  node.appendChild(textnode);
  return node;
}

window.addEventListener("DOMContentLoaded", () => {
  // Displays url on lobby page.
  document.getElementById("url").innerHTML =
    "Share the link!:<br>" + window.location.href;
});
