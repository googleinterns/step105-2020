const script = require('./join-room');

test('get roomId from given url', () => {
  expect(
    script.parseRoomId("https://localhost:8282/room?roomId=fhaskjdfhkaj")
  ).toBe("fhaskjdfhkaj")
})
