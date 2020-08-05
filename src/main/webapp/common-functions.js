function parseRoomId(url) {
    // Split url at '?' and saves second half (url parameter).
    let paramString = url.split('?')[1]; 
    let queryString = new URLSearchParams(paramString);
  
    // Saves value into roomId (pair[0] would be key).
    let roomId = "";
    for (let pair of queryString.entries()) {
        roomId = pair[1]; 
    }
  
    return roomId;
  }

function getRoomId() {
  let url = window.location.href;
  let paramString = parseRoomId(url);
  return paramString;
}

// Testing exports
exports.parseRoomId = parseRoomId;
