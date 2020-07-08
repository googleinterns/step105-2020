let players = [];

const createUser = (ev)=>{
    ev.preventDefault();  //to stop the form submitting
    let user = {
        username: document.getElementById('new-player-id').value,
        get userID() {
            // Base 36 (numbers + letters), and grab the first 9 characters after decimal.
            return '_' + Math.random().toString(36).substr(2, 9);
        }
    }
    players.push(user);

    //saving to cookies
    document.cookie = 'userID=' + user.userID + '; expires=' + new Date(2025, 0, 1).toUTCString();
}
document.addEventListener('DOMContentLoaded', ()=>{
    document.getElementById('new-player-btn').addEventListener('click', createUser);
});
