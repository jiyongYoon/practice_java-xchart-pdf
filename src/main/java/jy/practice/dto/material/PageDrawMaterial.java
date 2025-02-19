package jy.practice.dto.material;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDrawMaterial implements DrawMaterial {
  private PDType0Font font;
  private int fontSize;
  private int x; // 페이지 왼쪽에서 x 포인트 오른쪽에 위치 (값이 없으면 center에 배치)
  private int y; // 페이지 하단에서 y 포인트 위에 위치
}
