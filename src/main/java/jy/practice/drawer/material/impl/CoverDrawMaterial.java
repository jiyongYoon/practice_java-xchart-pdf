package jy.practice.drawer.material.impl;

import jy.practice.drawer.material.DrawMaterial;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoverDrawMaterial implements DrawMaterial {

  private PDImageXObject logoImage;
  private StringValueFont title;
  private StringValueFont title2;
  private StringValueFont companyName;
  private StringValueFont period;
  private StringValueFont printDate;
}
