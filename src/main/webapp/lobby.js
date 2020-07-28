const APP_ID = "1024158";
const CLIENT_KEY = "d15fbbe1c77552dc5097";
const PUSHER_APPLICATION_NAME = "song-guessing-game";
const PUSHER_GAME_CHANNEL_NAME = "start-game";
const PUSHER_ROUND_CHANNEL_NAME = "start-round";

window.addEventListener('DOMContentLoaded', ()=>{
  document.getElementById('start-game').addEventListener('click', startGame);
});

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
channel.bind(PUSHER_GAME_CHANNEL_NAME, function (data) {
  redirectToGamePage();
  loadRound();
});

channel.bind(PUSHER_ROUND_CHANNEL_NAME, function() {
  console.log("in pusher function");
  embedPlaylist();
  seconds = 30;
  Timer = setInterval("setTimer()", 1000);
  if (seconds == 0) {
    clearInterval(Timer);
    document.getElementById("timer").innerHTML = "TIME'S UP";
  }
});
  
function setTimer(){ 
  console.log("in set timer");
  now = new Date().getTime();
    document.getElementById("timer").innerHTML = seconds + "s ";
    seconds--;
    if (seconds <= 0) {
      clearInterval(Timer);
      document.getElementById("timer").innerHTML = "Round Over";      
    }
}

// Fetches list of usernames, appends each username to html list
function loadUsernames() {
    fetch('/user').then(response => response.json()).then((users) => {
      const userList = document.getElementById('user-list');
      users.forEach((username) => {
        userList.appendChild(createUsernameElement(username));
      })
    });
  }

// Appends text node to list node, returns list node
function createUsernameElement(username) {
    var node = document.createElement("li");
    var textnode = document.createTextNode(username);
    node.appendChild(textnode);
    return node;
  }

  async function loadRound() {
    await fetch("/round", {
      method: "PUT"
    });
  } 
