package com.catfood.demo.catcan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CatCanTextNormalizerTest {

  @Test
  void normalizeBrandOrNameRemovesAllWhitespace() {
    assertEquals("Cherie法麗", CatCanTextNormalizer.normalizeBrandOrName("Cherie 法麗"));
    assertEquals("Cherie法麗", CatCanTextNormalizer.normalizeBrandOrName("  Cherie  法麗  "));
    assertEquals("全照護主食罐", CatCanTextNormalizer.normalizeBrandOrName("全 照護 主食罐"));
    assertNull(CatCanTextNormalizer.normalizeBrandOrName(null));
  }
}
