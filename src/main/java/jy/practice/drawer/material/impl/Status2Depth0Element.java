package jy.practice.drawer.material.impl;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/**
 * 중처법 의무 이행 현황 - 세부 파일 페이지 중<br>
 * --> 대분류 객체
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status2Depth0Element {
  private PDType0Font font;
  private int fontSize;
  private String value;
  private List<Status2Depth1Element> status1Depth1ElementList;
}
