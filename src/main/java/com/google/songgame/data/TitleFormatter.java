package com.google.songgame.data;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.Arrays;

public final class TitleFormatter {

  private static final Function<MatchResult, String> replaceTargetGroups =
      (MatchResult matchResult) -> {
        String matchResultString = matchResult.group(1);
        if (checkIfStringInArrayOfStrings(matchResultString)) {
          return "";
        } else {
          return "(" + matchResultString + ")";
        }
      };

  private static boolean checkIfStringInArrayOfStrings(String s) {
    ArrayList<String> targetWords =
        new ArrayList<String>(
            Arrays.asList("feat", "remix", "ft", "music video", "official video", "lyric video"));
    for (String targetWord : targetWords) {
      if (s.contains(targetWord)) {
        return true;
      }
    }
    return false;
  }

  public static String formatVideoTitle(String title) {
    title = title.toLowerCase();
    Pattern pattern = Pattern.compile("\\(([^\\)]+)\\)");
    Matcher matcher = pattern.matcher(title);
    String result = matcher.replaceAll(replaceTargetGroups);
    System.out.println(result);
    return result;
  }
}
