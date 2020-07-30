package com.google.songgame.data;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.ArrayList;
import java.util.Arrays;

public final class TitleFormatter {

  public static String formatVideoTitle(String title) {
    String result = title.toLowerCase();
    result = removeInvalidParenthesesGroups(result);
    result = removeAllSquareBracketGroups(result);
    result = removeArtistPrefix(result);
    result = removeExcessWhiteSpace(result);

    return result;
  }

  private static String removeInvalidParenthesesGroups(String s) {
    Pattern pattern = Pattern.compile("\\(([^\\)]+)\\)");
    Matcher matcher = pattern.matcher(s);
    while (matcher.find()) {
      String currGroup = matcher.group(1);
      // if group inside of parentheses contains invalid word, delete it from string
      if (checkIfParenthesesGroupInvalid(currGroup)) {
        s = s.replace("(" + currGroup + ")", "");
        matcher = pattern.matcher(s);
      } else {
        // otherwise, start checking after the valid parentheses group
        matcher.region(matcher.end(), s.length());
      }
    }
    return s;
  }

  private static boolean checkIfParenthesesGroupInvalid(String s) {
    ArrayList<String> invalidWords =
        new ArrayList<String>(
            Arrays.asList(
                "feat",
                "remix",
                "ft",
                "music video",
                "official video",
                "lyric video",
                "official audio"));
    return invalidWords.stream().anyMatch(word -> s.contains(word));
  }

  private static String removeAllSquareBracketGroups(String s) {
    Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]");
    Matcher matcher = pattern.matcher(s);
    s = matcher.replaceAll("");
    return s;
  }

  private static String removeArtistPrefix(String input) {
    if (!input.contains("-")) {
      return input;
    }
    String result = input.split("-")[1];
    return result;
  }

  private static String removeExcessWhiteSpace(String input) {
    String result = input.trim();
    result = result.replaceAll(" +", " ");
    return result;
  }
}
