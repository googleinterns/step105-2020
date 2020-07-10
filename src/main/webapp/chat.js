const APP_ID = "1024158";
const CLIENT_KEY = "d15fbbe1c77552dc5097";
const PUSHER_APPLICATION_NAME = "song-guessing-game";
const PUSHER_CHAT_CHANNEL_NAME = "chat-update";

async function addToChat() {
  let chatInput = document.getElementById("chat-input").value;
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
  let newMessage = document.createElement("li");
  newMessage.appendChild(document.createTextNode(data.message));
  let chat = document.getElementById("chat");
  chat.appendChild(newMessage);
}

Pusher.logToConsole = false;

var pusher = new Pusher(CLIENT_KEY, {
  cluster: "us2",
});

var channel = pusher.subscribe(PUSHER_APPLICATION_NAME);
channel.bind(PUSHER_CHAT_CHANNEL_NAME, function (data) {
  updateChat(data);
});
