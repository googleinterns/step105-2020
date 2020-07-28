const createUser = async (ev) => {
    ev.preventDefault();  // To stop the form submitting.
    let user = {
        username: document.getElementById('new-player-id').value,
        // Base 36 (numbers + letters), and grab the first 9 characters after decimal.
        userId: '_' + Math.random().toString(36).substr(2, 9),
    }
    // Save userId to cookie.
    document.cookie = 'userId=' + user.userId + '; expires=' + new Date(2025, 0, 1).toUTCString();

    // Post user object
    await fetch("/user", {
        method: "POST",
        body: JSON.stringify(user),
        headers: {
          "Content-Type": "application/json",
        },
      });

    // Sends user to newuser.html on button click.
    window.location.href = 'newuser.html';
}

window.addEventListener('DOMContentLoaded', ()=>{
    // Creates user on button click.
    document.getElementById('new-player-btn').addEventListener('click', createUser);
});
