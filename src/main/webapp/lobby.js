// Fetches list of usernames, appends each username to html list
function loadUsernames() {
    fetch('/room').then(response => response.json()).then((users) => {
      const userList = document.getElementById('user-list');
      users.forEach((username) => {
        userList.appendChild(createUsernameElement(username));
      })
    });
  }

// Appends text node to list node, returns list node
function createUsernameElement(username) {
    var node = document.createElement("li");
    var textnode = document.createTextNode(username);
    node.appendChild(textnode);
    return node;
  }
