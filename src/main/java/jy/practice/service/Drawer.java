package jy.practice.service;

import java.io.IOException;
import jy.practice.dto.material.DrawMaterial;
import org.apache.pdfbox.pdmodel.PDDocument;

public interface Drawer<T extends DrawMaterial> {

  void draw(PDDocument document, T drawMaterial, float pageBorderMargin) throws IOException;

}
