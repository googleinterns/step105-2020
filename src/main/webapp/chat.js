async function addToChat() {
  let chatInput = document.getElementById("chat-input-box").value;
  let data = {
    message: chatInput,
  };

  let response = await fetch("/chat", {
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
  $("#chatbox").append(newChatItem);
  let elem = document.getElementById("chatbox");
  elem.scrollTop = elem.scrollHeight;
}

Pusher.logToConsole = false;

var pusher = new Pusher("d15fbbe1c77552dc5097", {
  cluster: "us2",
});

var channel = pusher.subscribe("spotify-game-app");
channel.bind("chat-update", function (data) {
  updateChat(data);
});
