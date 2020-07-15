const APP_ID = "1024158";
const CLIENT_KEY = "d15fbbe1c77552dc5097";
const PUSHER_APPLICATION_NAME = "song-guessing-game";
const PUSHER_CHAT_CHANNEL_NAME = "chat-update";

async function addToChat() {
  let chatInput = document.getElementById("chat-input-box").value;
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
  let message = data.message;
  let newChatItem = `<p class="message"><span class="username">User: </span>${message}</p>`;
  let chatbox = document.getElementById("chatbox");
  chatbox.insertAdjacentHTML("beforeend", newChatItem);
  // Autoscroll to bottom on chat update
  let elem = document.getElementById("chatbox");
  elem.scrollTop = elem.scrollHeight;
}

Pusher.logToConsole = false;

var pusher = new Pusher(CLIENT_KEY, {
  cluster: "us2",
});

var channel = pusher.subscribe(PUSHER_APPLICATION_NAME);
channel.bind(PUSHER_CHAT_CHANNEL_NAME, function (data) {
  updateChat(data);
});

function embedPlaylist(){
  fetch ('/game').then(response => response.json()).then((videoID) => {
    document.getElementById("player").src = "http://www.youtube.com/embed/" + videoID;
  });
  }
  
  // Add testing exports here
  