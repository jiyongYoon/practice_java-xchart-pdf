package jy.practice.drawer;

import java.io.IOException;
import jy.practice.drawer.material.DrawMaterial;
import org.apache.pdfbox.pdmodel.PDDocument;

public interface Drawer<T extends DrawMaterial> {

  /**
   * @param document pdf 문서
   * @param drawMaterial 문서 그릴 자료들
   * @param pageBorderMargin 문서 위아래좌우 가장자리 마진
   * @return 생성한 페이지 수
   * @throws IOException
   */
  int draw(PDDocument document, T drawMaterial, float pageBorderMargin) throws IOException;

}
