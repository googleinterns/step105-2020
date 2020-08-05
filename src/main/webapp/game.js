const APP_ID = "1024158";
const CLIENT_KEY = "d15fbbe1c77552dc5097";
const PUSHER_APPLICATION_NAME = "song-guessing-game";
const PUSHER_ROUND_CHANNEL_NAME = "start-round";
const ONE_SECOND = 1000;
const PUSHER_CHAT_CHANNEL_NAME_BASE = "chat-update-";
const CSS_MESSAGE_CLASS_DICT = {
  guess: "",
  spectator: "message-spectator",
  correct: "message-correct",
  announcement: "message-announcement",
};
const ROOM_ID = getRoomId();
var videoId = "";
var startTime = 0;
var endTime = 0;

window.addEventListener("DOMContentLoaded", () => {
  retrieveRound();
  createTimer();
  document.getElementById("start-round").addEventListener("click", loadRound);
});

// Connect Pusher
var pusher = new Pusher(CLIENT_KEY, {
  cluster: "us2",
});
var channel = pusher.subscribe(PUSHER_APPLICATION_NAME);

channel.bind(PUSHER_CHAT_CHANNEL_NAME_BASE + ROOM_ID, function (data) {
  updateChat(data);
});

// when the start round button is clicked
channel.bind(PUSHER_ROUND_CHANNEL_NAME, async function () {
  await retrieveRound();
  createTimer();
});

async function loadRound() {
  data = {
    roomId: ROOM_ID,
  };
  await fetch("/round", {
    method: "POST",
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
  let userPointsResponse = await fetch(`/game?roomId=${ROOM_ID}`);
  let userPoints = await userPointsResponse.json();
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

async function addToChat() {
  let chatInputField = document.getElementById("chat-input-box");
  let chatInput = chatInputField.value;

  chatInputField.value = "";
  chatInputField.focus();

  let data = {
    message: chatInput,
    roomId: ROOM_ID,
  };
  await fetch("/chat", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
}

document.onkeypress = function (e) {
  if (e.key === "Enter") {
    //checks whether the pressed key is "Enter"
    addToChat();
  }
};

async function retrieveRound() {
  let response = await fetch(`/round?roomId=${ROOM_ID}`);
  let roundMap = await response.json();
  videoId = roundMap.videoId;
  startTime = roundMap.startTime;
  endTime = roundMap.endTime;
  await embedVideo();
}

async function embedVideo() {
  // await retrieveRound();
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

function createTimer() {
  Timer = setInterval("setTimer()", ONE_SECOND);
}

function setTimer() {
  let timer = document.getElementById("timer");
  let now = new Date().getTime();
  if (startTime > 0 && now >= startTime) {
    timer.innerHTML =
      "Time left in round: " + Math.floor((endTime - now) / ONE_SECOND) + "s";
    if (now >= endTime) {
      clearInterval(Timer);
      timer.innerHTML = "Round Over";
    }
  }
}

window.addEventListener("DOMContentLoaded", () => {
  loadScore();
  retrieveRound();
});

// Add testing exports here
