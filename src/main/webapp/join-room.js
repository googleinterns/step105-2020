const createRoom = async (ev) => {
    ev.preventDefault();
    let room = {
      roomId: Math.random().toString(36).substr(2, 9)
    }
  
    // Post room object
    await fetch("/room", {
      method: "POST",
      body: JSON.stringify(room),
      headers: {
        "Content-Type": "application/json",
      },
    });
  
    // Sends user to room
    window.location.href = 'lobby.html?roomId=' + room.roomId;
}

window.addEventListener('DOMContentLoaded', ()=>{
    // Creates room on button click.
    document.getElementById('create-room-btn').addEventListener('click', createRoom);
});
