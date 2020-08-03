const APP_ID = "1024158";
const CLIENT_KEY = "d15fbbe1c77552dc5097";
const PUSHER_APPLICATION_NAME = "song-guessing-game";
const PUSHER_CHAT_CHANNEL_NAME = "chat-update";
const CSS_MESSAGE_CLASS_DICT = {
  guess: "",
  spectator: "message-spectator",
  correct: "message-correct",
  announcement: "message-announcement",
};

async function addToChat() {
  let chatInputField = document.getElementById("chat-input-box");
  let chatInput = chatInputField.value;

  chatInputField.value = "";
  chatInputField.focus();

  let data = {
    message: chatInput,
  };
  await fetch("/chat", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
}

function updateChat(data) {
  let newChatItem = createChatItem(data);
  let chatbox = document.getElementById("chatbox");
  chatbox.insertAdjacentHTML("beforeend", newChatItem);
  // Autoscroll to bottom on chat update
  let elem = document.getElementById("chatbox");
  elem.scrollTop = elem.scrollHeight;
  loadScore();
}

async function loadScore() {
  // MAKE A GET REQUEST TO LOAD THE SCORE
  let pointsResponse = await fetch("/game?points=true");
  let pointsJson = await pointsResponse.json();
  let userPoints = pointsJson.propertyMap;
  let users = Object.keys(userPoints);
  let scoreBox = document.getElementById("score-box");
  scoreBox.innerHTML = "";
  for (let user of users) {
    let newPointItem = `<p class="user-point"><span class="username">${user}: </span>${userPoints[user]}</p>`;
    scoreBox.insertAdjacentHTML("beforeend", newPointItem);
  }
}

function updateScore(data) {
  let newChatItem = createChatItem(data);
  let chatbox = document.getElementById("chatbox");
  chatbox.insertAdjacentHTML("beforeend", newChatItem);
  // Autoscroll to bottom on chat update
  let elem = document.getElementById("chatbox");
  elem.scrollTop = elem.scrollHeight;
}

function createChatItem(data) {
  let message = data.message;
  let username = data.username;
  let messageType = CSS_MESSAGE_CLASS_DICT[data.messageType];
  return `<p class="${messageType}"><span class="username">${username}: </span>${message}</p>`;
}

// Connect Pusher
Pusher.logToConsole = false;

var pusher = new Pusher(CLIENT_KEY, {
  cluster: "us2",
});

var channel = pusher.subscribe(PUSHER_APPLICATION_NAME);
channel.bind(PUSHER_CHAT_CHANNEL_NAME, function (data) {
  updateChat(data);
});

function embedPlaylist() {
  fetch("/game")
    .then((response) => response.json())
    .then((videoIdResponse) => {
      videoId = videoIdResponse;

      document.getElementById("player").src =
        "https://www.youtube.com/embed/" +
        videoId +
        "?version=3&end=10&loop=1&playlist=" +
        videoId +
        "&enablejsapi=1&autoplay=1&controls=0&modestbranding=1&disablekb=1";

      window.onYouTubeIframeAPIReady = function () {
        window.player = new window.YT.Player("player", {
          events: {
            onStateChange: onPlayerStateChange,
          },
          playerVars: {
            rel: 0,
          },
        });
      };
    });
}

function onPlayerStateChange(event) {
  if (event.data === YT.PlayerState.ENDED) {
    player.loadVideoById({
      videoId: videoId,
      startSeconds: 0,
      endSeconds: 10,
    });
  }
}

document.onkeypress = function (e) {
  if (e.key === "Enter") {
    //checks whether the pressed key is "Enter"
    addToChat();
  }
};

// Add testing exports here
