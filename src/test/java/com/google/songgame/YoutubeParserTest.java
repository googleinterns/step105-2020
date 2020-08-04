package com.google.songgame.data;

import org.junit.Assert;
import org.junit.rules.ExpectedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class YoutubeParserTest {

  private YoutubeParser youtubeParser;

  @Rule public ExpectedException thrown = ExpectedException.none();

  @Before
  public void init() {
    youtubeParser = new YoutubeParser();
  }

  // Test getPlaylistIdFromUrl
  @Test
  public void parsePlaylistUrlWithParameters() {
    String testPlaylistUrl =
        "https://www.youtube.com/playlist?list=PLbpi6ZahtOH7CkYdkWCsAVMcX1hoYy3TP&app=desktop";
    String playlistId = "PLbpi6ZahtOH7CkYdkWCsAVMcX1hoYy3TP";
    Assert.assertEquals(playlistId, youtubeParser.getPlaylistIdFromUrl(testPlaylistUrl));

    testPlaylistUrl =
        "https://www.youtube.com/playlist?list=PLbpi6ZahtOH4OiptAsB2BrGjIiITwrdqs&app=desktop";
    playlistId = "PLbpi6ZahtOH4OiptAsB2BrGjIiITwrdqs";
    Assert.assertEquals(playlistId, youtubeParser.getPlaylistIdFromUrl(testPlaylistUrl));
  }

  @Test
  public void parseYoutubeShareLink() {
    String testPlaylistUrl =
        "https://www.youtube.com/playlist?list=PLbpi6ZahtOH5AZzzL-piQQ2ryL6zitgqZ";
    String playlistId = "PLbpi6ZahtOH5AZzzL-piQQ2ryL6zitgqZ";
    Assert.assertEquals(playlistId, youtubeParser.getPlaylistIdFromUrl(testPlaylistUrl));

    testPlaylistUrl = "https://www.youtube.com/playlist?list=PLbpi6ZahtOH784jF-_UIbH2cT9l1C_ecA";
    playlistId = "PLbpi6ZahtOH784jF-_UIbH2cT9l1C_ecA";
    Assert.assertEquals(playlistId, youtubeParser.getPlaylistIdFromUrl(testPlaylistUrl));
  }

  @Test
  public void handleNonPlaylistLink() {
    String testPlaylistUrl =
        "https://www.youtube.com/watch?v=jxd42A3xFPs&list=PLbpi6ZahtOH784jF-_UIbH2cT9l1C_ecA&index=3&t=0s&app=desktop";
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(testPlaylistUrl + " is not a valid YouTube Playlist URL.");
    youtubeParser.getPlaylistIdFromUrl(testPlaylistUrl);

    testPlaylistUrl = "https://youtu.be/jxd42A3xFPs";
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(testPlaylistUrl + " is not a valid YouTube Playlist URL.");
    youtubeParser.getPlaylistIdFromUrl(testPlaylistUrl);
  }

  @Test
  public void handleNonYoutubeLink() {
    String testPlaylistUrl = "https://open.spotify.com/playlist/37i9dQZF1DX0XUsuxWHRQd";
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(testPlaylistUrl + " is not a valid YouTube Playlist URL.");
    youtubeParser.getPlaylistIdFromUrl(testPlaylistUrl);

    testPlaylistUrl =
        "https://music.apple.com/us/playlist/intro-to-alice-coltrane/pl.ea8a00ee10e94d7a9002583a337cbd3f";
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(testPlaylistUrl + " is not a valid YouTube Playlist URL.");
    youtubeParser.getPlaylistIdFromUrl(testPlaylistUrl);
  }

//   // Test extractVideoIdFromJson

//   @Test
//   public void handleNonIdData() {
//     String testData = ": [ {  \"kind\": \"youtube#playlistItem";
//     Assert.assertEquals("", youtubeParser.extractVideoIdFromJson(testData));

//     testData = ", \"channelId\": \"UCvceBgMIpKb4zK1ss-Sh90w";
//     Assert.assertEquals("", youtubeParser.extractVideoIdFromJson(testData));
//   }

//   @Test
//   public void parseVideoIdFromData() {
//     String testData =
//         "videoId\":\"GvgqDSnpRQM\"    }  }, \"contentDetails\": {  \"videoId\": \"GvgqDSnpRQM";
//     String videoId = "GvgqDSnpRQM";
//     Assert.assertEquals(videoId, youtubeParser.extractVideoIdFromJson(testData));
//   }

//   @Test
//   public void handleNonPlaylistId() {
//     String testData = "id\": \"UExCQ0YyREFDNkZGQjU3NERFLjU5NzE2QkNERURDRTE5NDc=";
//     Assert.assertEquals("", youtubeParser.extractVideoIdFromJson(testData));
//   }
// }
