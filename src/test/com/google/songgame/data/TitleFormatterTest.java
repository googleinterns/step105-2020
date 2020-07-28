package com.google.songgame;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/*
  https://music.youtube.com/playlist?list=RDCLAK5uy_kmPRjHDECIcuVwnKsx2Ng7fyNgFKWNJFs

*/

@RunWith(JUnit4.class)
public final class TitleFormatterTest {
  @Test
  public void bigboitest() {
    TitleFormatter titleFormatter = new TitleFormatter();
    String testString = "Life is Good (Official Music Video) (feat. Drake)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "life is good");

    testString = "Marvins Room";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "marvins room");

    testString = "Sativa (ft. Swae Lee)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "sativa");

    testString = "Get You (feat. Kali Uchis)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "get you");

    testString = "Promises (feat. Namiko, Miyagi & Miyagi & Namiko)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "promises");

    testString = "WHATS POPPIN [Remix] (feat. DaBaby, Tory Lanez & Lil Wayne";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "whats poppin");

    testString = "Juice WRLD ft. Marshmello - Come & Go (Official Audio)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "come & go");

    testString = "Savage (Remix) (feat. Beyoncé)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "come & go");

    testString = "BLACK PARADE";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "black parade");

    testString = "Roses (Imanbek Remix) [Latino Gang]";

    testString = "Rod Wave - Girl Of My Dreams (Official Music Video)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "girl of my dreams");

    testString = "ily (i love you baby) (feat. Emilee)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "ily (i love you baby)");

    testString = "death bed (coffee for your head)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "death bed (coffee for your head)");

    testString = "Gucci Mane - Both Sides feat. Lil Baby";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "both sides");

    testString = "YG - Swag (Official Music Video)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "swag");

    testString = "Chase B & Don Toliver - Cafeteria (feat. Gunna) (Official Video)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "cafeteria");
    
    testString = "NBA YoungBoy - ALL IN";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "all in");
    
    testString = "King Von - Why He Told (Official Video)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "why he told");

    testString = "Tattoo (Remix)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "tattoo");

    testString = "Djadja [Remix] (feat. Afro B)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "djadja");

    testString = "La Jeepeta Remix (Lyric Video)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "la jeepeta");
  }
}
