package jy.practice.example.test.component;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * 해당 객체에 데이터를 계속 추가하는 식으로 편하게 이미지, 글 등을 추가하도록 Wrapping 해놓은 객체
 * 테스트 용으로 구현해보았으나, 정렬 등의 어려움으로 table 형태로 하는 것으로 방향 선회
 */
@Getter
public class PdfContentStreamWrapper {

    private final PDDocument document;
    private PDPage currentPage;
    private PDPageContentStream contentStream;
    private PDRectangle pdRectangle;
    private float margin;
    private float pageWidth;
    private float pageHeight;

    // 현재 좌표
    private float currentX;
    private float currentY;

    // 점유 영역 관리
    private List<OccupiedArea> occupiedAreas;

    // 기본 상수
    public static final float DEFAULT_LEADING = 15; // 줄 간격
    public static final float DEFAULT_HORIZONTAL_GAP = 5; // 같은 줄 내 컨텐츠 사이 간격

    public PdfContentStreamWrapper(PDDocument document, PDPage page, PDRectangle pdRectangle, float margin) throws IOException {
        this.document = document;
        this.margin = margin;
        this.currentPage = page;
        this.pdRectangle = pdRectangle;

        PDRectangle mediaBox = page.getMediaBox();
        this.pageWidth = mediaBox.getWidth();
        this.pageHeight = mediaBox.getHeight();

        this.currentX = margin; // 좌측 마진
        this.currentY = pageHeight - margin; // 상단 마진
        this.contentStream = new PDPageContentStream(document, page);
        this.occupiedAreas = new ArrayList<>();
    }

    /**
     * 현재 Y 좌표에서 requiredHeight 만큼 사용할 공간이 부족하면 새 페이지를 생성합니다.
     */
    private void ensureVerticalSpace(float requiredHeight) throws IOException {
        if (currentY - requiredHeight < margin) {
            contentStream.close();
            PDPage newPage = new PDPage(pdRectangle);
            document.addPage(newPage);
            currentPage = newPage;

            contentStream = new PDPageContentStream(document, currentPage);

            PDRectangle mediaBox = newPage.getMediaBox();
            pageWidth = mediaBox.getWidth();
            pageHeight = mediaBox.getHeight();

            // 새 페이지 초기화
            currentX = margin;
            currentY = pageHeight - margin;
            occupiedAreas.clear();
        }
    }

    /**
     * 텍스트를 추가하며 점유 영역을 고려합니다.
     */
    public PdfContentStreamWrapper addText(String text, PDType0Font font, float fontSize) throws IOException {
        float maxWidth = pageWidth - currentX - margin; // 현재 X 위치에서 남은 너비 계산

        List<String> lines = wrapText(text, font, fontSize, maxWidth);

        for (String line : lines) {
            ensureVerticalSpace(fontSize + DEFAULT_LEADING);

            // 현재 위치에서 점유 영역과 겹치면 다음 줄로 이동
            while (isOverlapping(currentX, currentY - fontSize, maxWidth, fontSize)) {
                newLine(fontSize + DEFAULT_LEADING);
                maxWidth = pageWidth - currentX - margin; // 다시 남은 너비 계산
            }

            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(currentX, currentY);
            contentStream.showText(line);
            contentStream.endText();

            currentY -= (fontSize + DEFAULT_LEADING); // 다음 줄로 이동
        }

        return this;
    }

    /**
     * 텍스트를 단어 단위로 줄바꿈합니다.
     */
    private List<String> wrapText(String text, PDType0Font font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
            float lineWidth = font.getStringWidth(testLine) / 1000 * fontSize;

            if (lineWidth <= maxWidth) {
                currentLine.append(word).append(" ");
            } else {
                lines.add(currentLine.toString().trim());
                currentLine.setLength(0); // 초기화
                currentLine.append(word).append(" ");
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }

    /**
     * 이미지를 추가하며 점유 영역을 기록합니다.
     * 이미지가 오른쪽 끝을 넘어가거나, 추가될 영역이 이미 점유되어 있으면 자동으로 줄바꿈합니다.
     */
    public PdfContentStreamWrapper addImage(PDImageXObject image, float width, float height) throws IOException {
        // 오른쪽으로 이미지가 삐져나가는지 확인
        if (currentX + width > pageWidth - margin) {
            newLine(height + DEFAULT_LEADING);
        }
        // 현재 영역과 이미지가 겹치는지 확인. 겹친다면 줄바꿈
        while (isOverlapping(currentX, currentY - height, width, height)) {
            newLine(height + DEFAULT_LEADING);
        }
        ensureVerticalSpace(height);

        contentStream.drawImage(image, currentX, currentY - height, width, height);

        // 점유 영역 기록
        occupiedAreas.add(new OccupiedArea(currentX, currentY - height, width, height));

        // X 좌표 업데이트: 이미지 오른쪽으로 이동
        currentX += width + DEFAULT_HORIZONTAL_GAP;

        return this;
    }

    /**
     * 지정한 색상과 선 두께로 선을 그립니다.
     * 선의 영역이 기존 점유 영역과 겹치면 자동으로 줄바꿈하여 겹치지 않는 위치에 그립니다.
     */
    public PdfContentStreamWrapper addLineSeparator(Color color, float lineWidth) throws IOException {
        ensureVerticalSpace(DEFAULT_LEADING);

        // 선의 영역: x는 margin, 너비는 (pageWidth - 2*margin), 높이는 lineWidth로 가정
        while (isOverlapping(margin, currentY - lineWidth, pageWidth - 2 * margin, lineWidth)) {
            newLine(DEFAULT_LEADING);
        }

        contentStream.setStrokingColor(color);
        contentStream.setLineWidth(lineWidth);
        contentStream.moveTo(margin, currentY);
        contentStream.lineTo(pageWidth - margin, currentY);
        contentStream.stroke();

        // 점유 영역 기록
        occupiedAreas.add(new OccupiedArea(margin, currentY - lineWidth, pageWidth - 2 * margin, lineWidth));
        currentY -= DEFAULT_LEADING;
        currentX = margin;
        return this;
    }

    /**
     * 점유된 공간과 겹치는지 확인합니다.
     */
    private boolean isOverlapping(float x, float y, float width, float height) {
        for (OccupiedArea area : occupiedAreas) {
            if (area.isOverlapping(x, y, width, height)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 개발자가 원하는 수만큼 줄을 바꾸어 현재 좌표를 수직으로 이동합니다.
     *
     * @param spacing 이동할 수직 간격
     * @return PdfContentStreamWrapper 객체 (메서드 체이닝 가능)
     */
    public PdfContentStreamWrapper newLine(float spacing) throws IOException {
        ensureVerticalSpace(spacing);
        currentY -= spacing;
        // 줄바꿈 시 X 좌표를 왼쪽 마진으로 초기화
        currentX = margin;
        return this;
    }

    /**
     * PDF 저장 전 contentStream을 닫아 마무리 작업을 수행합니다.
     */
    public void close() throws IOException {
        contentStream.close();
    }

    /**
     * 내부 클래스: 점유 영역 관리
     */
    private static class OccupiedArea {
        private final float x;
        private final float y;
        private final float width;
        private final float height;

        public OccupiedArea(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean isOverlapping(float x, float y, float width, float height) {
            return !(x + width < this.x || x > this.x + this.width || y + height < this.y || y > this.y + this.height);
        }
    }
}
