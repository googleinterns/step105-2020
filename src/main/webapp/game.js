const APP_ID = "1024158";
const CLIENT_KEY = "d15fbbe1c77552dc5097";
const PUSHER_APPLICATION_NAME = "song-guessing-game";
const PUSHER_CHAT_CHANNEL_NAME = "chat-update";
const PUSHER_ROUND_CHANNEL_NAME = "start-round";
const PUSHER_GAME_CHANNEL_NAME = "start-game";
const ONE_SECOND = 1000;
const CSS_MESSAGE_CLASS_DICT = {
  guess: "",
  spectator: "message-spectator",
  correct: "message-correct",
  announcement: "message-announcement",
};
// TODO: @salilnadkarni, replace with userid from cookie (in datastore)
const USER_ID = "_" + Math.random().toString(36).substr(2, 9);
var videoId = "";
var startTime = 0;
var endTime = 0;

window.addEventListener('DOMContentLoaded', ()=>{
  console.log
  embedVideo();
  createTimer();
  document.getElementById('start-round').addEventListener('click', loadRound);
});

async function addToChat() {
  let chatInputField = document.getElementById("chat-input-box");
  let chatInput = chatInputField.value;
  
  chatInputField.value = "";
  chatInputField.focus();

  let data = {
    message: chatInput,
    userId: USER_ID,
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
channel.bind(PUSHER_CHAT_CHANNEL_NAME, function(data) {
  updateChat(data);
});

function embedVideo() {
  fetch('/round').then(response => response.json()).then((roundMap) => {
    videoId = roundMap.videoId;
    startTime = roundMap.startTime;
    endTime = roundMap.endTime;
    document.getElementById("player").src = "https://www.youtube.com/embed/" + videoId;
  });
}

// Start Round
async function loadRound() {
  await fetch("/round", {
    method: "PUT"
  });
} 

// when the start round button is clicked
channel.bind(PUSHER_ROUND_CHANNEL_NAME, function() {
  embedVideo();
  createTimer();
});


function createTimer(){
  Timer = setInterval("setTimer()", ONE_SECOND);
}
  
function setTimer(){ 
  let now = new Date().getTime();
if (startTime > 0 && now >= startTime){
    document.getElementById("timer").innerHTML = ((endTime - now) % ONE_SECOND ) + "s ";
    if (now >= endTime) {
      clearInterval(Timer);
      document.getElementById("timer").innerHTML = "Round Over";      
    }
}
}

document.onkeypress = function (e) {
  if (e.key === "Enter") {  //checks whether the pressed key is "Enter"
    addToChat();
  }
};
  
// Add testing exports here
