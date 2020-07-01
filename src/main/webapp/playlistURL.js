function getIdFromURL(url) {
    var start = url.lastIndexOf('/') + 1;
    var end = url.indexOf('?', start);
    return url.slice(start, end);
  }

 async function getRandomPreviewURL() {
    let playlist = await getPlaylist();
    let trackList = playlist.tracks.items;
    var randomTrack = trackList[Math.floor(Math.random() * trackList.length)];
    var previewURL = randomTrack.track.preview_url;
    return previewURL;
}

exports.getIdFromURL = getIdFromURL;
exports.getRandomPreviewURL = getRandomPreviewURL;
