package com.google.songgame.data;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.function.Function;
import java.util.ArrayList;

public final class TitleFormatter {
  private static final ArrayList<String> targetWords = new ArrayList<String>({
    "feat", "remix", "ft", "music video", "official video", "lyric video"
  });
  private static final Function<MatchResult,String> replaceTargetGroups = (MatchResult matchResult) -> {
    String matchResultString = matchResult.group(1);
    if (checkIfStringInArrayOfStrings(targetWords, matchResultString)) {
      return "";
    } else {
      return "(" + matchResultString + ")";
    }
  };

  private boolean checkIfStringInArrayOfStrings(ArrayList<String> possibleStrings, String s) {
    for (String possibleString : possibleStrings) {
      if (s.contains(possibleString)) {
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