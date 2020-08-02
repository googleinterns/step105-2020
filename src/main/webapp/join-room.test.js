const script = require('./common-functions');

test('get roomId from given url', () => {
  expect(
    script.parseRoomId("https://localhost:8282/lobby.html?roomId=fhaskjdfhkaj")
  ).toBe("fhaskjdfhkaj")
})

test('gets roomId without protocol', () => {
  expect(
    script.parseRoomId("localhost:8282/lobby.html?roomId=fhaskjdfhkaj")
  ).toBe("fhaskjdfhkaj")
})

test('empty when url does not contain question mark', () => {
  expect(
    script.parseRoomId("https://localhost:8282/lobby.htmlroomId=fhaskjdfhkaj")
  ).toBe("")
})

test('empty when no parameter is provided', () => {
  expect(
    script.parseRoomId("https://localhost:8282/lobby.html")
  ).toBe("")
})

test('empty when no roomId is provided', () => {
  expect(
    script.parseRoomId("https://localhost:8282/lobby.html?roomId=")
  ).toBe("")
})
