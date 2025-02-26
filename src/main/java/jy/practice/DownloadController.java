package jy.practice;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import jy.practice.example.Example;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloadController {

  private final Example example;

  @GetMapping
  public void download(HttpServletResponse response) throws IOException {
    System.out.println("Springboot Project ---> download method run!!");

    String fileName = "report.pdf";

    // 응답 헤더 설정
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

    example.run(Optional.empty(), Optional.of(response));

    System.out.println("------> Web export finished!! name = " + fileName);
  }

}
