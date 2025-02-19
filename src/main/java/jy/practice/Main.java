package jy.practice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import jy.practice.component.PDImageGenerator;
import jy.practice.component.PieChartGenerator;
import jy.practice.component.test.dto.SafetyHealthItem;
import jy.practice.dto.SapaCriteriaElementDto;
import jy.practice.dto.StatusDepth1Dto;
import jy.practice.dto.material.CoverDrawMaterial;
import jy.practice.dto.material.DashboardComponentElement;
import jy.practice.dto.material.DashboardDrawMaterial;
import jy.practice.dto.DashboardDto;
import jy.practice.dto.material.PageDrawMaterial;
import jy.practice.dto.material.SapaCriteriaDrawMaterial;
import jy.practice.dto.SapaCriteriaDto;
import jy.practice.dto.material.SapaImplStatus1DrawMaterial;
import jy.practice.dto.material.SapaImplStatus2DrawMaterial;
import jy.practice.dto.material.Status1Depth1Element;
import jy.practice.dto.material.Status1Depth2Element;
import jy.practice.dto.material.Status2Depth0Element;
import jy.practice.dto.material.Status2Depth1Element;
import jy.practice.dto.material.Status2Depth2Element;
import jy.practice.dto.material.Status2Depth2FileTableElement;
import jy.practice.dto.material.StringValueFont;
import jy.practice.service.CoverDrawer;
import jy.practice.service.DashboardDrawer;
import jy.practice.service.PageNumberDrawer;
import jy.practice.service.SapaCriteriaDrawer;
import jy.practice.service.SapaImplStatus1Drawer;
import jy.practice.service.SapaImplStatus2Drawer;
import jy.practice.store.DangerTagStore;
import jy.practice.store.FontStore;
import jy.practice.store.PDPageStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class Main {

  public static void main(String[] args) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    StringBuilder sb = new StringBuilder();
    BufferedReader br;

    //
    sb.setLength(0);
    br = new BufferedReader(new FileReader("./criteria.json"));

    String data2;
    while ((data2 = br.readLine()) != null) {
      sb.append(data2);
    }

    List<SafetyHealthItem> safetyHealthItems = objectMapper.readValue(sb.toString(), new TypeReference<List<SafetyHealthItem>>() {});

    //
    sb.setLength(0);
    br = new BufferedReader(new FileReader("./dashboard.json"));
    String data;
    while ((data = br.readLine()) != null) {
      sb.append(data);
    }

    DashboardDto dashboardDto = objectMapper.readValue(sb.toString(), DashboardDto.class);

    //
    sb.setLength(0);
    br = new BufferedReader(new FileReader("./sapastatus1.json"));
    String data3;
    while ((data3 = br.readLine()) != null) {
      sb.append(data3);
    }

    StatusDepth1Dto statusDepth1Dto = objectMapper.readValue(sb.toString(), StatusDepth1Dto.class);


    ////////////////// 문서 전체 그리기 테스트 /////////////////

    PDType0Font pdFont = FontStore.getPdFont();
    PDType0Font pdBoldFont = FontStore.getPdBoldFont();
    PDPage pdPage = PDPageStore.generateHorizontalPage();
    float MARGIN = 35f;
    float dashboardMargin = 35f;

    try (PDDocument document = new PDDocument()) {
      // 1. 커버
      PDImageXObject logoImage = PDImageGenerator.generate("my/logo/SafelyLogo.be87d102.png");
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
      SapaCriteriaDrawMaterial sapaCriteriaDrawMaterial = SapaCriteriaDrawMaterial.builder()
          .columnsOfWidth(new float[]{200, 250, 110, 110, 110})
          .headerValue(new String[]{
              "중대재해처벌법 항목",
              "세이플리 대응 문서",
              "목표 업로드 주기",
              "파일 개수",
              "총 목표 파일개수"
          })
          .safetyHealthItemList(safetyHealthItems)
          .font(FontStore.getPdFont())
          .boldFont(FontStore.getPdBoldFont())
          .bodyFontSize(8)
          .titleFontSize(10)
          .build();
      SapaCriteriaDrawer sapaCriteriaDrawer = new SapaCriteriaDrawer();
      sapaCriteriaDrawer.draw(document, sapaCriteriaDrawMaterial, MARGIN);

      // 3. 대시보드
      Font font = FontStore.getFont();
      font = font.deriveFont(25f);
      PDImageXObject documentImage = PDImageGenerator.generate("my/tag/document.png");
      PDImageXObject unclassifiedChartImage = PDImageGenerator.generate("my/tag/no-chart.png");
      List<DashboardComponentElement> dashboardComponentElementList = new ArrayList<>();
      List<SapaCriteriaDto> sapaCriteriaList = dashboardDto.getSapaCriteriaList();
      for (SapaCriteriaDto sapaCriteriaDto : sapaCriteriaList) {
        int totalCount = DashboardComponentElement.calculateTotalCount(
            sapaCriteriaDto.getCount(), sapaCriteriaDto.getAchievementRate());
        int chartsWidthAndHeight = 150;
        DashboardComponentElement dashboardComponentElement = DashboardComponentElement.builder()
            .title(sapaCriteriaDto.getName())
            .okCount(sapaCriteriaDto.getCount())
            .totalCount(totalCount)
            .chartImage(PDImageGenerator.generate(
                PieChartGenerator.generateDonutChart(chartsWidthAndHeight, chartsWidthAndHeight, sapaCriteriaDto.getCount(), totalCount, font)))
            .tagImage(DangerTagStore.pick(sapaCriteriaDto.getAchievementRate()))
            .build();
        dashboardComponentElementList.add(dashboardComponentElement);
      }
      DashboardComponentElement unclassifiedElement = DashboardComponentElement.builder()
          .title("미분류 파일")
          .okCount(dashboardDto.getUnclassifiedFileDto().getSize())
          .build();

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
          .unclassifiedElement(unclassifiedElement)
          .unclassifiedChartImage(unclassifiedChartImage)
          .build();
      DashboardDrawer dashboardDrawer = new DashboardDrawer();
      dashboardDrawer.draw(document, dashboardDrawMaterial, dashboardMargin);

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
      sapaImplStatus1Drawer.draw(document, sapaImplStatus1DrawMaterial, MARGIN);

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
          createSapaImplStatus2DrawMaterialDummy(columnsOfWidth, headerStr);

      SapaImplStatus2Drawer sapaImplStatus2Drawer = new SapaImplStatus2Drawer();
      sapaImplStatus2Drawer.draw(document, sapaImplStatus2DrawMaterial, MARGIN);

      // 6. 페이지 처리
      PageDrawMaterial pageDrawMaterial = PageDrawMaterial.builder()
          .font(FontStore.getPdFont())
          .fontSize(8)
          .y(30)
          .build();
      PageNumberDrawer pageNumberDrawer = new PageNumberDrawer();
      pageNumberDrawer.draw(document, pageDrawMaterial, MARGIN);

      document.save(new File("component-test.pdf"));
    }

  }

  private static SapaImplStatus2DrawMaterial createSapaImplStatus2DrawMaterialDummy(float[] columnsOfWidth,
      String[] headerStr) {
    Status2Depth0Element largeCategoryElement1 = generateLargeCategory(
        columnsOfWidth, headerStr);

    Status2Depth0Element largeCategoryElement2 = generateLargeCategory(
        columnsOfWidth, headerStr);

    SapaImplStatus2DrawMaterial sapaImplStatus2DrawMaterial = SapaImplStatus2DrawMaterial.builder()
        .status2Depth0ElementList(Arrays.asList(largeCategoryElement1, largeCategoryElement2))
        .font(FontStore.getPdFont())
        .build();
    return sapaImplStatus2DrawMaterial;
  }

  private static Status2Depth0Element generateLargeCategory(float[] columnsOfWidth,
      String[] headerStr) {
    Status2Depth1Element smallCategoryElement1 = generateSmallCategory(columnsOfWidth, headerStr);
    Status2Depth1Element smallCategoryElement2 = generateSmallCategory(columnsOfWidth, headerStr);
    Status2Depth1Element smallCategoryElement3 = generateSmallCategory(columnsOfWidth, headerStr);

    Status2Depth0Element largeCategoryElement = Status2Depth0Element.builder()
        .status1Depth1ElementList(Arrays.asList(smallCategoryElement1, smallCategoryElement2, smallCategoryElement3))
        .font(FontStore.getPdFont())
        .fontSize(FontStore.LARGE_CATEGORY_FONT_SIZE)
        .value("안전보건 경영방침 및 목표_" + UUID.randomUUID().toString().substring(0, 4))
        .build();
    return largeCategoryElement;
  }

  private static Status2Depth1Element generateSmallCategory(float[] columnsOfWidth,
      String[] headerStr) {
    Random random = new Random();
    int fileCount = random.nextInt(50);
    int smallCategoryCount = random.nextInt(4);
    int largeCategoryCount = random.nextInt(20);

    List<Status2Depth2FileTableElement> tableElementList = new ArrayList<>();

    for (int i = 0; i < fileCount; i++) {
      tableElementList.add(generateFileTableElementDummy(random));
    }

    Status2Depth2Element headerInfoElement = Status2Depth2Element.builder()
        .columnsOfWidth(columnsOfWidth)
        .headerValue(headerStr)
        .headerFont(FontStore.getPdBoldFont())
        .headerFontSize(10)
        .tableFont(FontStore.getPdFont())
        .tableFontSize(8)
        .status2Depth2FileTableElementList(tableElementList)
        .build();

    Status2Depth1Element smallCategoryElement = Status2Depth1Element.builder()
        .statusDepth2Element(headerInfoElement)
        .font(FontStore.getPdFont())
        .fontSize(FontStore.SMALL_CATEGORY_FONT_SIZE)
        .value("안전보건 경영방침_" + UUID.randomUUID().toString().substring(0, 4))
        .count(fileCount)
        .build();
    return smallCategoryElement;
  }

  private static Status2Depth2Element generateHeaderInfoDummy(float[] columnsOfWidth, String[] headerStr,
      Random random, int fileCount) {
    List<Status2Depth2FileTableElement> tableElementList = new ArrayList<>();

    for (int i = 0; i < fileCount; i++) {
      tableElementList.add(generateFileTableElementDummy(random));
    }

    return Status2Depth2Element.builder()
        .columnsOfWidth(columnsOfWidth)
        .headerValue(headerStr)
        .headerFont(FontStore.getPdBoldFont())
        .headerFontSize(10)
        .tableFont(FontStore.getPdFont())
        .tableFontSize(8)
        .status2Depth2FileTableElementList(tableElementList)
        .build();
  }

  private static List<Status2Depth0Element> generateLargeCategory(
      List<Status2Depth1Element> smallCategoryList,
      int largeCategoryCount) {
    List<Status2Depth0Element> largeCategoryList = new ArrayList<>();
    for (int i = 0; i < largeCategoryCount; i++) {
      largeCategoryList.add(Status2Depth0Element.builder()
          .status1Depth1ElementList(smallCategoryList)
          .font(FontStore.getPdFont())
          .fontSize(FontStore.LARGE_CATEGORY_FONT_SIZE)
          .value("안전보건 경영방침 및 목표_" + UUID.randomUUID().toString().substring(0, 4))
          .build()
      );
    }

    return largeCategoryList;
  }

  private static List<Status2Depth1Element> generateSmallCategory(int fileCount, int smallCategoryCount, Status2Depth2Element status2Depth2Element) {
    List<Status2Depth1Element> smallCategoryList = new ArrayList<>();
    for (int i = 0; i < smallCategoryCount; i++) {
      smallCategoryList.add(Status2Depth1Element.builder()
          .statusDepth2Element(status2Depth2Element)
          .font(FontStore.getPdFont())
          .fontSize(FontStore.SMALL_CATEGORY_FONT_SIZE)
          .value("안전보건 경영방침_" + UUID.randomUUID().toString().substring(0, 4))
          .count(fileCount)
          .build());
    }

    return smallCategoryList;
  }

  private static Status2Depth2FileTableElement generateFileTableElementDummy(Random random) {
    String uuid6 = UUID.randomUUID().toString().substring(0, 6);
    return Status2Depth2FileTableElement.builder()
        .fileName("안전보건 목표 및 추진계획서_" + uuid6)
        .lastUploadUser("김위즈_" + uuid6.substring(2))
        .lastUploadDate("2024-10-23 10:29")
        .classifyDate("2024-10-" + random.nextInt(31))
        .size(random.nextInt(5000))
        .build();
  }

  // perplexity version
  /*

  private static SapaImplStatus2DrawMaterial createSapaImplStatus2DrawMaterialDummy(float[] columnsOfWidth,
      String[] headerStr) {
    Random random = new Random();
    int largeCategoryCount = random.nextInt(20);

    SapaImplStatus2DrawMaterial sapaImplStatus2DrawMaterial = SapaImplStatus2DrawMaterial.builder()
        .status2Depth0ElementList(generateLargeCategory(largeCategoryCount, columnsOfWidth, headerStr))
        .font(FontStore.getPdFont())
        .build();
    return sapaImplStatus2DrawMaterial;
  }

  private static List<Status2Depth0Element> generateLargeCategory(
      int largeCategoryCount, float[] columnsOfWidth, String[] headerStr) {
    List<Status2Depth0Element> largeCategoryList = new ArrayList<>();
    Random random = new Random();

    for (int i = 0; i < largeCategoryCount; i++) {
      int smallCategoryCount = random.nextInt(4);
      largeCategoryList.add(Status2Depth0Element.builder()
          .status1Depth1ElementList(generateSmallCategory(smallCategoryCount, columnsOfWidth, headerStr))
          .font(FontStore.getPdFont())
          .fontSize(FontStore.LARGE_CATEGORY_FONT_SIZE)
          .value("안전보건 경영방침 및 목표_" + UUID.randomUUID().toString().substring(0, 4))
          .build()
      );
    }

    return largeCategoryList;
  }

  private static List<Status2Depth1Element> generateSmallCategory(int smallCategoryCount, float[] columnsOfWidth, String[] headerStr) {
    List<Status2Depth1Element> smallCategoryList = new ArrayList<>();
    Random random = new Random();

    for (int i = 0; i < smallCategoryCount; i++) {
      int fileCount = random.nextInt(50);
      Status2Depth2Element status2Depth2Element = generateHeaderInfoDummy(columnsOfWidth, headerStr, random, fileCount);

      smallCategoryList.add(Status2Depth1Element.builder()
          .statusDepth2Element(status2Depth2Element)
          .font(FontStore.getPdFont())
          .fontSize(FontStore.SMALL_CATEGORY_FONT_SIZE)
          .value("안전보건 경영방침_" + UUID.randomUUID().toString().substring(0, 4))
          .count(fileCount)
          .build());
    }

    return smallCategoryList;
  }

  private static Status2Depth2Element generateHeaderInfoDummy(float[] columnsOfWidth, String[] headerStr,
      Random random, int fileCount) {
    List<Status2Depth2FileTableElement> tableElementList = new ArrayList<>();

    for (int i = 0; i < fileCount; i++) {
      tableElementList.add(generateFileTableElementDummy(random));
    }

    return Status2Depth2Element.builder()
        .columnsOfWidth(columnsOfWidth)
        .headerValue(headerStr)
        .headerFont(FontStore.getPdBoldFont())
        .headerFontSize(10)
        .tableFont(FontStore.getPdFont())
        .tableFontSize(8)
        .status2Depth2FileTableElementList(tableElementList)
        .build();
  }

  private static Status2Depth2FileTableElement generateFileTableElementDummy(Random random) {
    String uuid6 = UUID.randomUUID().toString().substring(0, 6);
    return Status2Depth2FileTableElement.builder()
        .fileName("안전보건 목표 및 추진계획서_" + uuid6)
        .lastUploadUser("김위즈_" + uuid6.substring(2))
        .lastUploadDate("2024-10-23 10:29")
        .classifyDate("2024-10-" + (random.nextInt(31) + 1))
        .size(random.nextInt(5000))
        .build();
  }

   */

}