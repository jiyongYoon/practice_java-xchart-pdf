package jy.practice.store;

import java.awt.Color;

public class ColorStore {

  // table
  public final static Color BLUE_DARK = new Color(76, 129, 190);
  public final static Color BLUE_LIGHT_1 = new Color(186, 206, 230);
  public final static Color BLUE_LIGHT_2 = new Color(218, 230, 242);
  public final static Color GRAY_LIGHT_1 = new Color(245, 245, 245);
  public final static Color GRAY_LIGHT_2 = new Color(240, 240, 240);
  public final static Color GRAY_LIGHT_3 = new Color(216, 216, 216);


  // text
  public final static Color GRAY_DARK = new Color(130, 130, 130);


  // chart
  private static final Color gray = new Color(242, 242, 242);
  private static final Color red = new Color(230, 57, 91);
  private static final Color orange = new Color(255, 127, 35);
  private static final Color yellow = new Color(243, 207, 0);
  private static final Color green = new Color(145, 221, 74);
  private static final Color blue = new Color(102, 216, 178);

  public static Color getChartGray() {
    return gray;
  }

  public static Color pick(long percent) {
    if (0 <= percent && percent < 20) {
      return red;
    } else if (20 <= percent && percent < 40) {
      return orange;
    } else if (40 <= percent && percent < 60) {
      return yellow;
    } else if (60 <= percent && percent < 80) {
      return green;
    } else if (80 <= percent && percent <= 100) {
      return blue;
    } else {
      return null;
    }
  }
}
