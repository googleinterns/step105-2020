package com.google.songgame.data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/*
  https://music.youtube.com/playlist?list=RDCLAK5uy_kmPRjHDECIcuVwnKsx2Ng7fyNgFKWNJFs

*/

@RunWith(JUnit4.class)
public final class TitleFormatterTest {

  @Test
  public void simpleCapitalization() {
    TitleFormatter titleFormatter = new TitleFormatter();

    String testString = "Marvins Room";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "marvins room");

    testString = "BLACK PARADE";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "black parade");
  }

  @Test
  public void removeOneGroup() {
    TitleFormatter titleFormatter = new TitleFormatter();

    String testString = "Sativa (ft. Swae Lee)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "sativa");

    testString = "Get You (feat. Kali Uchis)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "get you");

    testString = "Promises (feat. Namiko, Miyagi & Miyagi & Namiko)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "promises");

    testString = "Tattoo (Remix)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "tattoo");
  }

  @Test
  public void removeMultipleGroups() {
    TitleFormatter titleFormatter = new TitleFormatter();

    String testString = "Savage (Remix) (feat. Beyoncé)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "savage");

    testString = "Life is Good (Official Music Video) (feat. Drake)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "life is good");
  }

  @Test
  public void removeMultipleGroupsSquareAndParen() {
    TitleFormatter titleFormatter = new TitleFormatter();

    String testString = "WHATS POPPIN [Remix] (feat. DaBaby, Tory Lanez & Lil Wayne)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "whats poppin");

    testString = "Djadja [Remix] (feat. Afro B)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "djadja");

    testString = "Roses (Imanbek Remix) [Latino Gang]";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "roses");
  }

  @Test
  public void removeValidGroups() {
    TitleFormatter titleFormatter = new TitleFormatter();

    String testString = "ily (i love you baby) (feat. Emilee)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "ily (i love you baby)");

    testString = "death bed (coffee for your head)";
    Assert.assertEquals(
        titleFormatter.formatVideoTitle(testString), "death bed (coffee for your head)");
  }

  @Test
  public void removeArtistPrefix() {
    TitleFormatter titleFormatter = new TitleFormatter();

    String testString = "NBA YoungBoy - ALL IN";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "all in");
  }

  @Test
  public void removeArtistPrefixAndGroup() {
    TitleFormatter titleFormatter = new TitleFormatter();

    String testString = "Juice WRLD ft. Marshmello - Come & Go (Official Audio)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "come & go");

    testString = "Rod Wave - Girl Of My Dreams (Official Music Video)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "girl of my dreams");

    testString = "YG - Swag (Official Music Video)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "swag");

    testString = "Chase B & Don Toliver - Cafeteria (feat. Gunna) (Official Video)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "cafeteria");

    testString = "King Von - Why He Told (Official Video)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "why he told");
  }

  // TODO: @salilnadkarni add error checking for titles like this
  @Test
  @Ignore("TitleFormatter unable to handle words not in brackets/parens")
  public void removeUnenclosedKeywords() {
    TitleFormatter titleFormatter = new TitleFormatter();

    String testString = "La Jeepeta Remix (Lyric Video)";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "la jeepeta");

    testString = "Gucci Mane - Both Sides feat. Lil Baby";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "both sides");

    testString = "ENEE - Supalonely (Audio) ft. Gus Dapperton";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "supalonely");

    testString = "BLACKPINK - 'How You Like That' M/V";
    Assert.assertEquals(titleFormatter.formatVideoTitle(testString), "how you like that");
  }
}
