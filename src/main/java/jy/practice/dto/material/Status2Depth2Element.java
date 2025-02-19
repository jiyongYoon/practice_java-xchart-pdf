package jy.practice.dto.material;

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
 * --> 테이블(파일데이터) 헤더 정보 객체
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status2Depth2Element {
  private float[] columnsOfWidth;
  private String[] headerValue;
  private PDType0Font headerFont;
  private int headerFontSize;
  private PDType0Font tableFont;
  private int tableFontSize;

  List<Status2Depth2FileTableElement> status2Depth2FileTableElementList;
}
