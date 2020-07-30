const script = require('./join-room');

test('get roomId from given url', () => {
  expect(
    script.parseRoomId("https://localhost:8282/lobby.html?roomId=fhaskjdfhkaj")
  ).toBe("fhaskjdfhkaj")
})

test('get roomId from given url', () => {
  expect(
    script.parseRoomId("localhost:8282/lobby.html?roomId=fhaskjdfhkaj")
  ).toBe("fhaskjdfhkaj")
})

test('get roomId from given url', () => {
  expect(
    script.parseRoomId("https://localhost:8282/lobby.htmlroomId=fhaskjdfhkaj")
  ).toBe("")
})

test('get roomId from given url', () => {
  expect(
    script.parseRoomId("https://localhost:8282/lobby.html")
  ).toBe("")
})

test('get roomId from given url', () => {
  expect(
    script.parseRoomId("https://localhost:8282/lobby.html?roomId=")
  ).toBe("")
})
