package com.catfood.demo.catcan.service.parse;

import com.catfood.demo.catcan.dto.CatCanDraftDTO;

interface PlatformShareParser {

  SharePlatform platform();

  void parse(String rawInput, String url, String textWithoutUrl, CatCanDraftDTO draft);
}
