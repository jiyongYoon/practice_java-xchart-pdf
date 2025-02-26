package jy.practice;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import jy.practice.example.Example;

public class Main {

  public static void main(String[] args) throws IOException {
    System.out.println("Java Project ---> main method run!!");

    String outputFilePath = "./outputs/example.pdf";
    File file = new File(outputFilePath);

    Example example = new Example(new ObjectMapper());
    example.run(Optional.of(file), Optional.empty());

    System.out.println("------> File Generated!! path = " + outputFilePath);
  }
}