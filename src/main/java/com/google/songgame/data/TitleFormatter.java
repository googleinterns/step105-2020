package com.google.songgame.data;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.Arrays;

public final class TitleFormatter {

  public static String formatVideoTitle(String title) {
    String result = title.toLowerCase();

    Pattern pattern = Pattern.compile("\\(([^\\)]+)\\)");
    Matcher matcher = pattern.matcher(result);
    result = matcher.replaceAll(replaceTargetGroups);

    pattern = Pattern.compile("\\[([^\\]]+)\\]");
    matcher = pattern.matcher(result);
    result = matcher.replaceAll((MatchResult matchResult) -> "");

    result = removeArtistPrefix(result);
    result = removeExcessWhiteSpace(result);

    return result;
  }

  private static boolean checkIfStringInArrayOfStrings(String s) {
    ArrayList<String> targetWords =
        new ArrayList<String>(
            Arrays.asList(
                "feat",
                "remix",
                "ft",
                "music video",
                "official video",
                "lyric video",
                "official audio"));
    for (String targetWord : targetWords) {
      if (s.contains(targetWord)) {
        return true;
      }
    }
    return false;
  }

  private static final Function<MatchResult, String> replaceTargetGroups =
      (MatchResult matchResult) -> {
        String matchResultString = matchResult.group(1);
        if (checkIfStringInArrayOfStrings(matchResultString)) {
          return "";
        } else {
          return "(" + matchResultString + ")";
        }
      };

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
