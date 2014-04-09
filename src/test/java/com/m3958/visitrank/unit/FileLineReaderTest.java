package com.m3958.visitrank.unit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.json.JsonObject;

import com.m3958.visitrank.Utils.FileLineReader;
import com.m3958.visitrank.Utils.FileLineReader.FindLineResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FileLineReaderTest {
  
  private Path tf = Paths.get("flrt.txt");
  
  @Before
  public void setup() throws IOException{
    if (Files.exists(tf)) {
      Files.delete(tf);
    }
    Files.createFile(tf);
  }
  
  @After
  public void after() throws IOException{
    if (Files.exists(tf)) {
      Files.delete(tf);
    }
  }

  @Test
  public void t1() throws IOException {
    writesome(1000);
    FileLineReader flr = new FileLineReader(tf.toString());
    FindLineResult lr = flr.getLineByPosition(tf.toFile().length() / 2);
    System.out.println(lr);
  }

  @Test
  public void t2() throws IOException {
    FileLineReader flr = new FileLineReader(tf.toString());
    FindLineResult lr = flr.getLineByPosition(tf.toFile().length() / 2);
    Assert.assertNull(lr);
  }
  
  /**
   * only one line
   * @throws IOException
   */
  @Test
  public void t3() throws IOException {
    writesome(1);
    FileLineReader flr = new FileLineReader(tf.toString());
    FindLineResult lr = flr.getLineByPosition(tf.toFile().length() / 2);
    Assert.assertEquals(0, getT(lr.getLine()));
  }
  
  /**
   * two line
   * @throws IOException
   */
  @Test
  public void t4() throws IOException {
    writesome(2);
    FileLineReader flr = new FileLineReader(tf.toString());
    FindLineResult lr = flr.getLineByPosition(tf.toFile().length() / 2);
    Assert.assertEquals(1, getT(lr.getLine()));
  }
  
  @Test
  public void t5() throws IOException{
    writesome(2);
    FileLineReader flr = new FileLineReader(tf.toString());
    FindLineResult result = flr.getLogItem("xx0", 0);
    Assert.assertEquals(0, getT(result.getLine()));
  }
  
  @Test
  public void t6() throws IOException{
    writesome(2);
    FileLineReader flr = new FileLineReader(tf.toString());
    FindLineResult result = flr.getLogItem("xx1", 1);
    Assert.assertEquals(1, getT(result.getLine()));
  }
  
  @Test
  public void t7() throws IOException, InterruptedException{
    long times = 10000*1000;
    writesome(times);
    Thread.sleep(100);
    long i = 47800;
    long start = System.currentTimeMillis();
    FileLineReader flr = new FileLineReader(tf.toString());
    FindLineResult result = flr.getLogItem("xx" + i, i);
    System.out.println("cost: " + (System.currentTimeMillis() - start) + "ms, in " + times);
    Assert.assertEquals(i, getT(result.getLine()));
  }
  
  @Test
  public void t8() throws IOException{
    writesome(100);
    for(long i=0;i<10;i++){
      findAll(i);
    }
  }
  
  private void findAll(long i) throws IOException{
    FileLineReader flr = new FileLineReader(tf.toString());
    FindLineResult result = flr.getLogItem("xx"+ i, i);
    Assert.assertEquals(i, getT(result.getLine()));

  }
  
  private long getT(String line){
    JsonObject jo = new JsonObject(line);
    return jo.getLong("t");
  }
  
  private void writesome(long num) throws FileNotFoundException{
    PrintWriter pw = new PrintWriter(tf.toFile());
    for (int i = 0; i < num; i++) {
      DBObject dbo = new BasicDBObject();
      dbo.put("u", "xx" + i);
      dbo.put("t", Long.valueOf(i));
      pw.println(dbo.toString());
    }
    pw.close();
  }
}