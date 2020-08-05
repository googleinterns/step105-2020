const APP_ID = "1024158";
const CLIENT_KEY = "d15fbbe1c77552dc5097";
const PUSHER_APPLICATION_NAME = "song-guessing-game";
const PUSHER_ROUND_CHANNEL_NAME = "start-round";
const ROOM_ID = getRoomId();
const PUSHER_LOBBY_CHANNEL_NAME = "user-list";

window.addEventListener("DOMContentLoaded", () => {
  document.getElementById("start-game").addEventListener("click", startGame);
});

function redirectToGamePage() {
  let url = window.location.href;
  let paramString = parseRoomId(url);
  window.location.href = `game.html?roomId=${paramString}`;
}

// Connect to Pusher
var pusher = new Pusher(CLIENT_KEY, {
  cluster: "us2",
});
var channel = pusher.subscribe(PUSHER_APPLICATION_NAME);

channel.bind(PUSHER_ROUND_CHANNEL_NAME, function () {
  redirectToGamePage();
});

async function startGame() {
  data = {
    roomId: ROOM_ID,
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
  window.location.href = `game.html?roomId=${ROOM_ID}`;
}

// Fetches list of usernames, appends each username to html list.
function loadUsernames() {
  fetch(`/room?roomId=${ROOM_ID}`)
    .then((response) => response.json())
    .then((users) => {
      const userList = document.getElementById("user-list");
      users.forEach((username) => {
        userList.appendChild(createUsernameElement(username));
      });
    });
}
channel.bind(PUSHER_LOBBY_CHANNEL_NAME, function (data) {
  var list = document.getElementById("user-list");
  while (list.hasChildNodes()) {
    list.removeChild(list.firstChild);
  }
  loadUsernames();
});

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
    "Invite other players to join your room with this link:<br>" + window.location.href;
});
