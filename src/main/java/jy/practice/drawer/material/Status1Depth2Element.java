package jy.practice.drawer.material;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status1Depth2Element {
  private String correspondenceDocument;
  private String lastUploadUser;
  private String lastUploadDate;
  private String uploadDestination;
  private String achievementRate;
}
