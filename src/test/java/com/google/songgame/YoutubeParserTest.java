package com.google.songgame.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunsWith(JUnit4.classs)
public class YoutubeParserTest {
   
  private YoutubeParser youtubeParser;

  @Before
  public void init(){
    youtubeParser = new YoutubeParser();
  }

  // Test getPlaylistIdFromUrl
  @Test
  public  parsePlaylistUrlWithParameters{
    String testPlaylistUrl = 
        "https://www.youtube.com/watch?v=VGwtqDRWH8g&list=PLbpi6ZahtOH7-yGg3A9160XFWpXxQyNLK&app=desktop";
    String playlistId = "PLbpi6ZahtOH7-yGg3A9160XFWpXxQyNL";
    Assert assertEquals(youtubeParser.getPlaylistIdFromUrl(testString), playlistId)

    testPlaylistUrl = 
      "https://www.youtube.com/playlist?list=PLbpi6ZahtOH4OiptAsB2BrGjIiITwrdqs&app=desktop";
    playlistId = "PLbpi6ZahtOH4OiptAsB2BrGjIiITwrdqs";
    Assert assertEquals(youtubeParser.getPlaylistIdFromUrl(testString), playlistId)
  }

  @Test
  public  parseYoutubeShareLink{
    String testPlaylistUrl =
       "https://www.youtube.com/playlist?list=PLbpi6ZahtOH5AZzzL-piQQ2ryL6zitgqZ";
    String playlistId = "PLbpi6ZahtOH5AZzzL-piQQ2ryL6zitgqZ";
    Assert assertEquals(youtubeParser.getPlaylistIdFromUrl(testString), playlistId)

    testPlaylistUrl = 
      "https://www.youtube.com/playlist?list=PLbpi6ZahtOH784jF-_UIbH2cT9l1C_ecA";
    playlistId = "";
    Assert assertEquals(youtubeParser.getPlaylistIdFromUrl(testString), playlistId)
  }

  @Test
  public  handleNonPlaylistLink{
    String testPlaylistUrl = 
       "https://www.youtube.com/watch?v=jxd42A3xFPs&list=PLbpi6ZahtOH784jF-_UIbH2cT9l1C_ecA&index=3&t=0s&app=desktop";
    String playlistId = "";
    Assert assertEquals(youtubeParser.getPlaylistIdFromUrl(testString), playlistId)

    testPlaylistUrl = "https://youtu.be/jxd42A3xFPs";
    playlistId = "";
    Assert assertEquals(youtubeParser.getPlaylistIdFromUrl(testString), playlistId)
  }

  @Test
  public  handleNonYoutubeLink{
    String testPlaylistUrl = "https://open.spotify.com/playlist/37i9dQZF1DX0XUsuxWHRQd";
    String playlistId = "";
    Assert assertEquals(youtubeParser.getPlaylistIdFromUrl(testString), playlistId)

    testPlaylistUrl = "https://music.apple.com/us/playlist/intro-to-alice-coltrane/pl.ea8a00ee10e94d7a9002583a337cbd3f";
    playlistId = "";
    Assert assertEquals(youtubeParser.getPlaylistIdFromUrl(testString), playlistId)
  }
  
  @Test
  public  handleScreenSpecificLink{
    String testPlaylistUrl = "";
    String playlistId = "";
    Assert assertEquals(youtubeParser.getPlaylistIdFromUrl(testString), playlistId)

    testPlaylistUrl = "";
    playlistId = "";
    Assert assertEquals(youtubeParser.getPlaylistIdFromUrl(testString), playlistId)
  }

  // Test extractVideoIdFromJson

  @Test
  public  handleYoutubeShareLink{
    String testPlaylistUrl = ": [
      {
        \"kind\": \"youtube#playlistItem";
    String playlistId = ",
    \"channelId\": \"UCvceBgMIpKb4zK1ss-Sh90w";
    Assert assertEquals(youtubeParser.extractVideoIdFromJson(testString), playlistId)

    testPlaylistUrl = "videoId\": \"GvgqDSnpRQM\"
  }
},
\"contentDetails\": {
  \"videoId\": \"GvgqDSnpRQM";
    playlistId = ,
    "id\": \"UExCQ0YyREFDNkZGQjU3NERFLjU5NzE2QkNERURDRTE5NDc=\";
    Assert assertEquals(youtubeParser.extractVideoIdFromJson(testString), playlistId)
  }
    
}