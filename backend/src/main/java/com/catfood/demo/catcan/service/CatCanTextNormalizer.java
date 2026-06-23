package com.catfood.demo.catcan.service;

/** Normalizes cat-can text fields before persist or display in drafts. */
public final class CatCanTextNormalizer {

  private CatCanTextNormalizer() {}

  /** Removes all whitespace (leading, trailing, and internal) from brand or name. */
  public static String normalizeBrandOrName(String value) {
    if (value == null) {
      return null;
    }
    return value.replaceAll("\\s", "");
  }
}
