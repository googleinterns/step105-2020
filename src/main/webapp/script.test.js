const script = require('./script');
const fs = require('fs');
const path = require('path');
const html = fs.readFileSync(path.resolve(__dirname, '../webapp/index.html'), 'utf8');

jest
    .dontMock('fs');

test('extract playlist id from address bar url', () => {
  expect(
    script.getIdFromURL("https://open.spotify.com/playlist/37i9dQZF1DX28ZIZjK8SGt")
  ).toBe("37i9dQZF1DX28ZIZjK8SGt")
})

test('extract playlist id from url that spotify gives', () => {
  expect(
    script.getIdFromURL("https://open.spotify.com/playlist/37i9dQZF1DX28ZIZjK8SGt?si=_4J20xQJQxyB1ld1ZxOJQg")
  ).toBe("37i9dQZF1DX28ZIZjK8SGt")
})

test('does not accept non-spotify playlist links', () => {
  expect(
  script.getIdFromURL("https://open.spotify.com/track/7t86fVeDAd63ThaR0ZkxXS?si=XhyN7KA5QAimaXB3SF-G_g")  
  ).toBe(undefined)
})