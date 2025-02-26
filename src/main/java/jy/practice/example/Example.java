package jy.practice.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import jy.practice.Main;
import jy.practice.utils.factory.PDImageFactory;
import jy.practice.utils.factory.PieChartFactory;
import jy.practice.drawer.impl.CoverDrawer;
import jy.practice.drawer.impl.DashboardDrawer;
import jy.practice.drawer.impl.PageNumberDrawer;
import jy.practice.drawer.impl.SapaCriteriaDrawer;
import jy.practice.drawer.impl.SapaImplStatus1Drawer;
import jy.practice.drawer.impl.SapaImplStatus2Drawer;
import jy.practice.drawer.material.impl.CoverDrawMaterial;
import jy.practice.drawer.material.impl.DashboardComponentElement;
import jy.practice.drawer.material.impl.DashboardDrawMaterial;
import jy.practice.drawer.material.impl.PageDrawMaterial;
import jy.practice.drawer.material.impl.SapaCriteriaDepth1Element;
import jy.practice.drawer.material.impl.SapaCriteriaDepth2Element;
import jy.practice.drawer.material.impl.SapaCriteriaDrawMaterial;
import jy.practice.drawer.material.impl.SapaImplStatus1DrawMaterial;
import jy.practice.drawer.material.impl.SapaImplStatus2DrawMaterial;
import jy.practice.drawer.material.impl.Status1Depth1Element;
import jy.practice.drawer.material.impl.Status1Depth2Element;
import jy.practice.drawer.material.impl.Status2Depth0Element;
import jy.practice.drawer.material.impl.Status2Depth1Element;
import jy.practice.drawer.material.impl.Status2Depth2Element;
import jy.practice.drawer.material.impl.Status2Depth2FileTableElement;
import jy.practice.drawer.material.impl.StringValueFont;
import jy.practice.utils.store.ColorStore;
import jy.practice.utils.store.DangerTagStore;
import jy.practice.utils.store.FontStore;
import jy.practice.example.dto.DashboardDto;
import jy.practice.example.dto.SafetyHealthItem;
import jy.practice.example.dto.SapaCriteriaDto;
import jy.practice.example.dto.SapaCriteriaElementDto;
import jy.practice.example.dto.StatusDepth1Dto;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.PieChart;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Example {

  private final ObjectMapper objectMapper;

  /**
   * @param optionalFile 파일로 저장하려면 해당 파라미터로 File 객체 전달
   * @param optionalHttpServletResponse Web 다운로드하려면 해당 파라미터로 HttpServletResponse 객체 전달 (Header, ContentType 세팅된)
   * @throws IOException
   */
  public void run(Optional<File> optionalFile, Optional<HttpServletResponse> optionalHttpServletResponse) throws IOException {
    StringBuilder sb = new StringBuilder();
    BufferedReader br;

    //
    sb.setLength(0);
    br = getBufferedReader("example/data/criteria.json");

    String data2;
    while ((data2 = br.readLine()) != null) {
      sb.append(data2);
    }

    List<SafetyHealthItem> safetyHealthItems =
        objectMapper.readValue(sb.toString(), new TypeReference<List<SafetyHealthItem>>() {});

    //
    sb.setLength(0);
    br = getBufferedReader("example/data/dashboard.json");
    String data;
    while ((data = br.readLine()) != null) {
      sb.append(data);
    }

    DashboardDto dashboardDto = objectMapper.readValue(sb.toString(), DashboardDto.class);

    //
    sb.setLength(0);
    br = getBufferedReader("example/data/sapastatus1.json");
    String data3;
    while ((data3 = br.readLine()) != null) {
      sb.append(data3);
    }

    StatusDepth1Dto statusDepth1Dto = objectMapper.readValue(sb.toString(), StatusDepth1Dto.class);

    ////////////////// 문서 전체 그리기 /////////////////

    PDType0Font pdFont = FontStore.getPdFont();
    PDType0Font pdBoldFont = FontStore.getPdBoldFont();
    float MARGIN = 35f;
    float dashboardMargin = 35f;
    List<String> titleList = new ArrayList<>();

    try (PDDocument document = new PDDocument()) {
      drawReport(safetyHealthItems, dashboardDto, statusDepth1Dto, pdFont, pdBoldFont, MARGIN,
          dashboardMargin, titleList, document);

      if (optionalFile.isPresent()) {
        document.save(optionalFile.get());
      } else if (optionalHttpServletResponse.isPresent()) {
        HttpServletResponse httpServletResponse = optionalHttpServletResponse.get();
        try (OutputStream os = httpServletResponse.getOutputStream()) {
          document.save(os);
        }
      } else {
        String message = "One of the 'outputFile' or 'httpServletResponse' must required!!";
        System.out.println(message);
        throw new RuntimeException(message);
      }
    }
  }

  private static BufferedReader getBufferedReader(String filePath) {
    BufferedReader br;
    br = new BufferedReader(new InputStreamReader(
        Objects.requireNonNull(Example.class.getClassLoader().getResourceAsStream(filePath)),
        StandardCharsets.UTF_8
      )
    );
    return br;
  }

  private static void drawReport(List<SafetyHealthItem> safetyHealthItems, DashboardDto dashboardDto,
      StatusDepth1Dto statusDepth1Dto, PDType0Font pdFont, PDType0Font pdBoldFont, float MARGIN,
      float dashboardMargin, List<String> titleList, PDDocument document) throws IOException {
    // 1. 커버
    String logoFilePath = "example/logo/SafelyLogo.be87d102.png";

    byte[] logoByteArray = IOUtils.toByteArray(Objects.requireNonNull(
        Main.class.getClassLoader().getResourceAsStream(logoFilePath)));

    PDImageXObject logoImage = PDImageFactory.generate(logoByteArray, logoFilePath.split("\\.")[0]);
    CoverDrawMaterial coverDrawMaterial = CoverDrawMaterial.builder()
        .logoImage(logoImage)
        .title(StringValueFont.builder().value("중대재해처벌법").font(pdBoldFont).fontSize(50).build())
        .title2(
            StringValueFont.builder().value("의무 이행 보고서").font(pdBoldFont).fontSize(50).build())
        .companyName(
            StringValueFont.builder().value("위즈코어").font(pdBoldFont).fontSize(25).build())
        .period(StringValueFont.builder().value("[2차] 2024-01-01 ~ 2024-12-31").font(pdFont)
            .fontSize(25).build())
        .printDate(
            StringValueFont.builder().value(LocalDate.now().toString()).font(pdFont).fontSize(20)
                .build())
        .build();
    CoverDrawer coverDrawer = new CoverDrawer();
    coverDrawer.draw(document, coverDrawMaterial, MARGIN);

    // 2. 중대재해처벌법 의무 이행 기준
    List<SapaCriteriaDepth1Element> sapaCriteriaDepth1ElementList = safetyHealthItems.stream()
        .map(safetyHealthItem -> SapaCriteriaDepth1Element.builder()
            .name(safetyHealthItem.getName())
            .row1Color(ColorStore.GRAY_LIGHT_1)
            .row2Color(ColorStore.GRAY_LIGHT_2)
            .firstRowBackgroundColor(ColorStore.GRAY_LIGHT_3)
            .sapaCriteriaDepth2ElementList(safetyHealthItem.getElements().stream()
                .map(element -> SapaCriteriaDepth2Element.builder()
                    .name(element.getName())
                    .period(element.getPeriod())
                    .value(element.getValue())
                    .count(element.getCount())
                    .achievementRate(element.getAchievementRate())
                    .build())
                .collect(Collectors.toList()))
            .build())
        .collect(Collectors.toList());

    SapaCriteriaDrawMaterial sapaCriteriaDrawMaterial = SapaCriteriaDrawMaterial.builder()
        .columnsOfWidth(new float[]{200, 250, 110, 110, 110})
        .headerValue(new String[]{
            "중대재해처벌법 항목",
            "세이플리 대응 문서",
            "목표 업로드 주기",
            "파일 개수",
            "총 목표 파일개수"
        })
        .headerTextColor(Color.WHITE)
        .headerBackgroundColor(ColorStore.BLUE_DARK)
        .sapaCriteriaDepth1ElementList(sapaCriteriaDepth1ElementList)
        .font(pdFont)
        .boldFont(pdBoldFont)
        .bodyFontSize(8)
        .titleFontSize(10)
        .build();
    SapaCriteriaDrawer sapaCriteriaDrawer = new SapaCriteriaDrawer();
    int sapaCriteriaDrawnPageCount = sapaCriteriaDrawer.draw(document, sapaCriteriaDrawMaterial,
        MARGIN);
    addTitle(titleList, SapaCriteriaDrawer.SAPA_TITLE_VALUE, sapaCriteriaDrawnPageCount);

    // 3. 대시보드
    Font font = FontStore.getFont();
    font = font.deriveFont(25f);
    Color componentBorderColor = ColorStore.GRAY_LIGHT_3;

    String documentFilePath = "example/tags/document.png";
    byte[] documentByteArray = IOUtils.toByteArray(Objects.requireNonNull(
        Main.class.getClassLoader().getResourceAsStream(documentFilePath)));

    PDImageXObject documentImage = PDImageFactory.generate(documentByteArray, documentFilePath.split("\\.")[0]);

    String unclassifiedChartFilePath = "example/tags/no-chart.png";
    byte[] unclassifiedChartByteArray = IOUtils.toByteArray(Objects.requireNonNull(
        Main.class.getClassLoader().getResourceAsStream(unclassifiedChartFilePath)));

    PDImageXObject unclassifiedChartImage = PDImageFactory.generate(unclassifiedChartByteArray, unclassifiedChartFilePath.split("\\.")[0]);
    List<DashboardComponentElement> dashboardComponentElementList = new ArrayList<>();
    List<SapaCriteriaDto> sapaCriteriaList = dashboardDto.getSapaCriteriaList();
    for (SapaCriteriaDto sapaCriteriaDto : sapaCriteriaList) {
      int totalCount = DashboardComponentElement.calculateTotalCount(
          sapaCriteriaDto.getCount(), sapaCriteriaDto.getAchievementRate());
      int chartsWidthAndHeight = 150;
      PieChart donutChart = PieChartFactory.generateDonutChart(
          chartsWidthAndHeight, chartsWidthAndHeight, sapaCriteriaDto.getCount(),
          totalCount, font, ColorStore.pick(sapaCriteriaDto.getAchievementRate()),
          ColorStore.getChartGray());
      byte[] donutChartByteArray = BitmapEncoder.getBitmapBytes(donutChart, BitmapFormat.PNG);
      DashboardComponentElement dashboardComponentElement = DashboardComponentElement.builder()
          .componentBorderColor(componentBorderColor)
          .title(sapaCriteriaDto.getName())
          .okCount(sapaCriteriaDto.getCount())
          .totalCount(totalCount)
          .countTextColor(ColorStore.GRAY_DARK)
          .chartImage(PDImageFactory.generate(donutChartByteArray, "chart"))
          .tagImage(DangerTagStore.pick(sapaCriteriaDto.getAchievementRate()))
          .build();
      dashboardComponentElementList.add(dashboardComponentElement);
    }
    DashboardComponentElement unclassifiedElement = DashboardComponentElement.builder()
        .componentBorderColor(componentBorderColor)
        .countTextColor(ColorStore.GRAY_DARK)
        .title("미분류 파일")
        .okCount(dashboardDto.getUnclassifiedFileDto().getSize())
        .chartImage(unclassifiedChartImage)
        .build();
    dashboardComponentElementList.add(unclassifiedElement);

    DashboardDrawMaterial dashboardDrawMaterial = DashboardDrawMaterial.builder()
        .insideChartFont(font)
        .font(pdFont)
        .fontSize(8)
        .boldFont(pdBoldFont)
        .boldFontSize(8)
        .roundIndex(2)
        .roundStartDate("2024-01-01")
        .roundEndDate("2024-12-31")
        .xColSize(12)
        .xColCount(16)
        .rowHeight(10f)
        .componentInterval(2.5f)
        .documentImage(documentImage)
        .dashboardComponentElementList(dashboardComponentElementList)
//          .unclassifiedElement(unclassifiedElement)
//          .unclassifiedChartImage(unclassifiedChartImage)
        .build();
    DashboardDrawer dashboardDrawer = new DashboardDrawer();
    int dashboardDrawnPageCount = dashboardDrawer.draw(document, dashboardDrawMaterial,
        dashboardMargin);
    addTitle(titleList, DashboardDrawer.DASHBOARD_TITLE_VALUE, dashboardDrawnPageCount);

    // 4. 세부1
    List<Status1Depth2Element> status1Depth2ElementList = new ArrayList<>();
    for (SapaCriteriaElementDto sapaCriteriaElementDto : statusDepth1Dto.getSapaCriteriaElementDtoList()) {
      Status1Depth2Element element = Status1Depth2Element.builder()
          .correspondenceDocument(sapaCriteriaElementDto.getName())
          .lastUploadUser(sapaCriteriaElementDto.getLastUploadedUser().getUserName())
          .lastUploadDate(sapaCriteriaElementDto.getUpdatedDate())
          .uploadDestination(
              String.format("전체 차수 기간 (%3d/%3d개)", sapaCriteriaElementDto.getCount(),
                  sapaCriteriaElementDto.getValue()))
          .achievementRate(String.valueOf(sapaCriteriaElementDto.getAchievementRate()))
          .build();
      status1Depth2ElementList.add(element);
    }
    Status1Depth1Element status1Depth1Element = Status1Depth1Element.builder()
        .name(statusDepth1Dto.getSapaCriteriaDto().getName())
        .row1Color(ColorStore.GRAY_LIGHT_1)
        .row2Color(ColorStore.GRAY_LIGHT_2)
        .firstRowBackgroundColor(ColorStore.GRAY_LIGHT_3)
        .statusDept2ElementList(status1Depth2ElementList)
        .build();

    SapaImplStatus1DrawMaterial sapaImplStatus1DrawMaterial = SapaImplStatus1DrawMaterial.builder()
        .columnsOfWidth(new float[]{200, 220, 80, 100, 100, 80})
        .headerValue(new String[]{
            "중대재해처벌법 항목",
            "세이플리 대응 문서",
            "마지막 업로드",
            "마지막 업로드 날짜",
            "업로드 목표",
            "업로드 달성률"
        })
        .headerTextColor(Color.WHITE)
        .headerBackgroundColor(ColorStore.BLUE_DARK)
        .font(pdFont)
        .boldFont(pdBoldFont)
        .bodyFontSize(8)
        .headerFontSize(10)
        .status1Depth1ElementList(
            // 임시로... 실제 api 붙일때는 리스트로 불러올 고민 필요
            Arrays.asList(
                status1Depth1Element, status1Depth1Element, status1Depth1Element,
                status1Depth1Element,
                status1Depth1Element, status1Depth1Element, status1Depth1Element,
                status1Depth1Element,
                status1Depth1Element, status1Depth1Element, status1Depth1Element,
                status1Depth1Element,
                status1Depth1Element, status1Depth1Element, status1Depth1Element,
                status1Depth1Element,
                status1Depth1Element, status1Depth1Element, status1Depth1Element,
                status1Depth1Element
            ))
        .build();
    SapaImplStatus1Drawer sapaImplStatus1Drawer = new SapaImplStatus1Drawer();
    int sapaImplStatus1DrawnPageCount = sapaImplStatus1Drawer.draw(document, sapaImplStatus1DrawMaterial,
        MARGIN);
    addTitle(titleList, SapaImplStatus1Drawer.SapaImplStatus1_TITLE_VALUE, sapaImplStatus1DrawnPageCount);

    // 5. 세부2
    float[] columnsOfWidth = new float[]{50f, 250f, 110f, 130f, 130f, 100f};
    String[] headerStr = new String[]{
        "NO",
        "파일이름",
        "생성자",
        "업로드 날짜",
        "분류 기준일",
        "크기"
    };
    SapaImplStatus2DrawMaterial sapaImplStatus2DrawMaterial =
        createSapaImplStatus2DrawMaterialDummy(columnsOfWidth, headerStr, pdFont, pdBoldFont);

    SapaImplStatus2Drawer sapaImplStatus2Drawer = new SapaImplStatus2Drawer();
    int sapaImplStatus2DrawnPageCount = sapaImplStatus2Drawer.draw(document, sapaImplStatus2DrawMaterial,
        MARGIN);
    addTitle(titleList, SapaImplStatus2Drawer.SapaImplStatus2_TITLE_VALUE, sapaImplStatus2DrawnPageCount);

    // 6. 페이지 처리
    String[] titleArr = titleList.toArray(new String[0]);
    PageDrawMaterial pageDrawMaterial = PageDrawMaterial.builder()
        .font(pdFont)
        .fontSize(8)
        .y(15)
        .titleList(titleArr)
        .build();
    PageNumberDrawer pageNumberDrawer = new PageNumberDrawer();
//      pageNumberDrawer.draw(document, pageDrawMaterial, MARGIN);
    pageNumberDrawer.drawWithTitle(document, pageDrawMaterial, 330);
  }

  private static void addTitle(List<String> titleList, String titleValue, int pageCount) {
    for (int i = 0; i < pageCount; i++) {
      titleList.add(titleValue);
    }
  }

  private static SapaImplStatus2DrawMaterial createSapaImplStatus2DrawMaterialDummy(float[] columnsOfWidth,
      String[] headerStr, PDType0Font pdFont, PDType0Font pdBoldFont) {
    Status2Depth0Element largeCategoryElement1 = generateLargeCategory(
        columnsOfWidth, headerStr, pdFont, pdBoldFont);

    Status2Depth0Element largeCategoryElement2 = generateLargeCategory(
        columnsOfWidth, headerStr, pdFont, pdBoldFont);

    SapaImplStatus2DrawMaterial sapaImplStatus2DrawMaterial = SapaImplStatus2DrawMaterial.builder()
        .status2Depth0ElementList(Arrays.asList(largeCategoryElement1, largeCategoryElement2))
        .headerTextColor(Color.WHITE)
        .headerBackgroundColor(ColorStore.BLUE_DARK)
        .font(pdFont)
        .build();
    return sapaImplStatus2DrawMaterial;
  }

  private static Status2Depth0Element generateLargeCategory(float[] columnsOfWidth,
      String[] headerStr, PDType0Font pdFont, PDType0Font pdBoldFont) {
    Status2Depth1Element smallCategoryElement1 = generateSmallCategory(columnsOfWidth, headerStr, pdFont, pdBoldFont);
    Status2Depth1Element smallCategoryElement2 = generateSmallCategory(columnsOfWidth, headerStr, pdFont, pdBoldFont);
    Status2Depth1Element smallCategoryElement3 = generateSmallCategory(columnsOfWidth, headerStr, pdFont, pdBoldFont);

    Status2Depth0Element largeCategoryElement = Status2Depth0Element.builder()
        .status1Depth1ElementList(Arrays.asList(smallCategoryElement1, smallCategoryElement2, smallCategoryElement3))
        .font(pdFont)
        .fontSize(FontStore.LARGE_CATEGORY_FONT_SIZE)
        .value("안전보건 경영방침 및 목표_" + UUID.randomUUID().toString().substring(0, 4))
        .build();
    return largeCategoryElement;
  }

  private static Status2Depth1Element generateSmallCategory(float[] columnsOfWidth,
      String[] headerStr, PDType0Font pdFont, PDType0Font pdBoldFont) {
    Random random = new Random();
    int fileCount = random.nextInt(50);

    List<Status2Depth2FileTableElement> tableElementList = new ArrayList<>();

    for (int i = 0; i < fileCount; i++) {
      tableElementList.add(generateFileTableElementDummy(random));
    }

    Status2Depth2Element headerInfoElement = Status2Depth2Element.builder()
        .columnsOfWidth(columnsOfWidth)
        .headerValue(headerStr)
        .headerFont(pdBoldFont)
        .headerFontSize(10)
        .tableFont(pdFont)
        .tableFontSize(8)
        .status2Depth2FileTableElementList(tableElementList)
        .build();

    Status2Depth1Element smallCategoryElement = Status2Depth1Element.builder()
        .statusDepth2Element(headerInfoElement)
        .font(pdFont)
        .fontSize(FontStore.SMALL_CATEGORY_FONT_SIZE)
        .value("안전보건 경영방침_" + UUID.randomUUID().toString().substring(0, 4))
        .count(fileCount)
        .build();
    return smallCategoryElement;
  }

  private static Status2Depth2FileTableElement generateFileTableElementDummy(Random random) {
    String uuid6 = UUID.randomUUID().toString().substring(0, 6);
    return Status2Depth2FileTableElement.builder()
        .fileName("안전보건 목표 및 추진계획서_" + uuid6)
        .lastUploadUser("김위즈_" + uuid6.substring(2))
        .lastUploadDate("2024-10-23 10:29")
        .classifyDate("2024-10-" + random.nextInt(31))
        .size(random.nextInt(5000))
        .row1Color(ColorStore.GRAY_LIGHT_1)
        .row2Color(ColorStore.GRAY_LIGHT_2)
        .build();
  }

}
