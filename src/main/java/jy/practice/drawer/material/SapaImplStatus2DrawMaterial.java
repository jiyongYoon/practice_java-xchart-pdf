package jy.practice.drawer.material;

import java.awt.Color;
import java.util.List;
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
public class SapaImplStatus2DrawMaterial implements DrawMaterial {
  private PDType0Font font;
  private Color headerTextColor;
  private Color headerBackgroundColor;
  private List<Status2Depth0Element> status2Depth0ElementList;
}
