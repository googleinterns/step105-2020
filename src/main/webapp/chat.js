async function addToChat() {
  let chatInput = document.getElementById("chat-input").value;
  // let data = `message=${chatInput}&name=Jill`;

  let data = {
    message: chatInput,
  };

  let response = await fetch("/chat", {
    method: "POST", // or 'PUT'
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
  //   let responseJSON = await response.json();
  //   console.log(responseJSON);
}

function updateChat(data) {
  let newMessage = document.createElement("li");
  newMessage.appendChild(document.createTextNode(data.message));
  let chat = document.getElementById("chat");
  chat.appendChild(newMessage);
}

// // Enable pusher logging - don't include this in production
Pusher.logToConsole = true;

var pusher = new Pusher("d15fbbe1c77552dc5097", {
  cluster: "us2",
});

var channel = pusher.subscribe("spotify-game-app");
channel.bind("chat-update", function (data) {
  console.log(data);
  updateChat(data);
});
