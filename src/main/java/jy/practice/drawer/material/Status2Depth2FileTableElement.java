package jy.practice.drawer.material;

import java.awt.Color;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 중처법 의무 이행 현황 - 세부 파일 페이지 중<br>
 * --> 테이블(파일데이터) 실제 데이터 객체
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status2Depth2FileTableElement {
  private String fileName;
  private String lastUploadUser;
  private String lastUploadDate;
  private String classifyDate;
  private double size;
  private Color row1Color;
  private Color row2Color;
}
