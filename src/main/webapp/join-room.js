const createRoom = async (ev) => {
    ev.preventDefault();
    let room = {
      roomId: Math.random().toString(36).substr(2, 9)
    }
  
    // Post room object.
    await fetch("/room", {
      method: "POST",
      body: JSON.stringify(room),
      headers: {
        "Content-Type": "application/json",
      },
    });
  
    // Sends user to room.
    window.location.href = 'lobby.html?roomId=' + room.roomId;
}

const joinRoom = async (ev) => {
  ev.preventDefault();

  // Get url from user input.
  let url = document.getElementById('room-url-id').value;
  let roomId = parseRoomId(url);

  let room = {
    roomId: roomId
  }
  
  // Put room object.
  fetch("/room", {
    method: "PUT",
    body: JSON.stringify(room),
    headers: {
      "Content-Type": "application/json",
    },
  });

  // Sends user to entered room.
  window.location.href = url;
}

window.addEventListener('DOMContentLoaded', ()=>{
    // Creates room on button click.
    document.getElementById('create-room-btn').addEventListener('click', createRoom);
    // Joins room on button click.
    document.getElementById('join-room-btn').addEventListener('click', joinRoom);
});
