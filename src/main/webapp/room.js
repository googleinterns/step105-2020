const createUser = async (ev) => {
    ev.preventDefault();  // To stop the form submitting.
    let user = {
        username: document.getElementById('new-player-id').value,
        // Base 36 (numbers + letters), and grab the first 9 characters after decimal.
        userId: '_' + Math.random().toString(36).substr(2, 9)
    }
    // Save userId to cookie.
    document.cookie = 'userId=' + user.userId + '; expires=' + new Date(2025, 0, 1).toUTCString();
    await fetch('/room', {
        method: 'POST',
        body: JSON.stringify(user),
    });
}
document.addEventListener('DOMContentLoaded', ()=>{
    document.getElementById('new-player-btn').addEventListener('click', createUser);
});
