package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

public class FileParser implements Runnable {
  private BlockingQueue<Word> queue;
  private File file;

  public FileParser(BlockingQueue<Word> queue, File file) {
    this.queue = queue;
    this.file = file;
  }

  @Override
  public void run() {
    System.out.println("File parser running");
    String line = null;
    
    String fileName = file.getName();
    if(Thread.currentThread().getName().equalsIgnoreCase("queryThread")) {
      fileName = "queryFile";
    }
    
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

      while((line = br.readLine()) != null) {
        if(line.length() > 0) {
          
          String[] words = line.trim().toLowerCase().replaceAll("[^A-Za-z0-9 ]", "").split(" ");
          int len = words.length;
          int count = 0;
          int leftOver = len % 3;
          String shingle = "";
          for(String s : words) {
            shingle += s + " ";
            len--;
            if(len >= leftOver){
              count++;
              if(count == 3) {
                queue.put(new Word(fileName, shingle.trim()));
                count = 0;
                shingle = "";
              }
            }else if(len == 0) {
              queue.put(new Word(fileName, shingle.trim()));
              shingle = "";
            }
          }
        }
      }
      queue.put(new Poison(fileName, "end"));
      System.out.println("File Parser finish");
      br.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
