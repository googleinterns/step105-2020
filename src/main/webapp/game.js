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
// TODO: @salilnadkarni, replace with userid from cookie (in datastore)
const USER_ID = "_" + Math.random().toString(36).substr(2, 9);

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

function embedPlaylist() {
  fetch('/game').then(response => response.json()).then((videoID) => {
    videoId = videoID;

    var tag = document.createElement('script');
    var firstScript = document.getElementsByTagName('script')[0];

    tag.src = 'https://www.youtube.com/iframe_api';
    firstScript.parentNode.insertBefore(tag, firstScript);

    document.getElementById("player").src = "https://www.youtube.com/embed/" + videoID + 
        "?version=3&end=10&loop=1&playlist=" + videoID + "&enablejsapi=1&autoplay=1&controls=0&modestbranding=1&disablekb=1";
  window.onYouTubeIframeAPIReady = function() {
    window.player = new window.YT.Player('player', {
      events: {
        'onStateChange': onPlayerStateChange
      }
    });
  }
});
}


function onPlayerStateChange(event) {
  if (event.data == YT.PlayerState.ENDED) {
    player.loadVideoById({
      videoId: videoId,
      startSeconds: 0,
      endSeconds: 10
    });
    }
  }

document.onkeypress = function (e) {
  if (e.key === "Enter") {  //checks whether the pressed key is "Enter"
    addToChat();
  }
};


  
// Add testing exports here