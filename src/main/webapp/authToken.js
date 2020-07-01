async function getAuthToken() {
    let authTokenRequest = await fetch('/game', {method: 'GET'});
    let authTokenResponse = await authTokenRequest.json();
    let authToken = authTokenResponse[0];
    return authToken;
  }
  
exports.getAuthToken = getAuthToken;