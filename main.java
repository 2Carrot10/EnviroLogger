import controlP5.*;
import java.util.*;
import processing.serial.*;

Serial myPort;
ControlP5 cp5;
Textarea consoleTextArea;
Textlabel header;
Textlabel textLabelXRangeControl;
Textlabel textLabelYRangeControl;
CheckBox checkBox;
boolean autoScaleX=false;
boolean autoScaleY=false;
String consoleLog="";//full console log
String consoleLogComments="";
String consoleLogData="";


enum ConsoleMode{allLogs,data,comments}
ConsoleMode consoleMode = ConsoleMode.allLogs;

int baudRate = 19200;
int portNumber = 0;
ArrayList<dataPoint> data = new ArrayList<>();
  
   Graph graph = new Graph(25, 60, 750, 360);
float fontSize=10;
float fontSizeTextFunction=20;// text size to be used for text(). Not general font.

void setup() { 
          textSize(fontSizeTextFunction);
  size(800, 800);//can't call function using variables defined above for some reason.
  cp5 = new ControlP5(this);
  PFont font = createFont("arial",20);
  
  
  if (Serial.list().length>portNumber) {
    String portName = Serial.list()[portNumber];
    if(myPort!=null)myPort=null;
    myPort = new Serial(this, portName, baudRate);
            println("serialreloaded");
  }

  checkBox = cp5.addCheckBox("checkBox")
                .setPosition(280, 430)
                .setSize(50, 50)
                .setItemsPerRow(1)
                .setSpacingColumn(80)
                .setSpacingRow(20)
                .addItem("auto scale x", 0)
                .setFont(font)
                .addItem("auto scale y", 50)
                ;
  
       textLabelXRangeControl = cp5.addTextlabel("xRangeControl")
                    .setText("x range:")
                    .setPosition(20,440)
                    .setColorValue(0xffffffff)
                    .setFont(font);  
                                      
                    
                    cp5.addTextfield("xMin")
     .setPosition(100,430)
     .setSize(80,40)
     .setFont(font)
     .setFocus(true)
     .setColor(color(255,0,0))
     .setAutoClear(false)
     ;
     
cp5.addTextfield("xMax")
     .setPosition(190,430)
     .setSize(80,40)
     .setFont(font)
     .setFocus(true)
     .setColor(color(255,0,0))
     .setAutoClear(false)
     ;
                    
                    
                    
     textLabelYRangeControl = cp5.addTextlabel("yRangeControl")
                    .setText("y range:")
                    .setPosition(20,510)
                    .setColorValue(0xffffffff)
                    .setFont(font);
                    
                    
   cp5.addTextfield("yMin")
     .setPosition(100,500)
     .setSize(80,40)
     .setFont(font)
     .setFocus(true)
     .setColor(color(255,0,0))
     .setAutoClear(false)
     ;
     
cp5.addTextfield("yMax")
     .setPosition(190,500)
     .setSize(80,40)
     .setFont(font)
     .setFocus(true)
     .setColor(color(255,0,0))
     .setAutoClear(false)
     ;
                    
                    
    header = cp5.addTextlabel("label")
                    .setText("EnviroLog")
                    .setPosition(400-textWidth("EnviroLog"),10)
                    .setColorValue(0xffffffff)
                    .setFont(createFont("Georgia",20));
     
cp5.addTextfield("baud rate")
.setPosition(530,430)
     .setSize(80, 40)
     .setFont(font)
     .setFocus(true)
     .setColor(color(255,0,0))
     ;
     
cp5.addTextfield("port number")
.setPosition(530,500)
     .setSize(80, 40)
     .setFont(font)
     .setFocus(true)
     .setColor(color(255,0,0))
     ;
     
     
  cp5.addButton("reloadserial")
     .setValue(1)
  .setPosition(650,460)
     .setSize(150, 50)
      .setFont(font)
     ;                
  
   cp5.addButton("Quantize")
     .setValue(1)
    .setPosition(400, 460)
     .setFont(font)
     .setSize(120, 60)
     ;
     
     cp5.get(Textfield.class,"baud rate").setText(Integer.toString(baudRate));
     cp5.get(Textfield.class,"port number").setText(Integer.toString(portNumber));
     
     
consoleTextArea = cp5.addTextarea("txt")
                  .setPosition(225,575)
                  .setSize(550,200)
                  .setFont(font)
                  .setLineHeight(30)
                  .setColor(color(128))
                  .setColorBackground(color(55))
                  .setColorForeground(color(255,100));
            
             
     cp5.get(Textfield.class,"xMin").setText(Float.toString(graph.valueLeftSide));
     cp5.get(Textfield.class,"xMax").setText(Float.toString(graph.valueRightSide));
     cp5.get(Textfield.class,"yMin").setText(Float.toString(graph.valueBottom));
     cp5.get(Textfield.class,"yMax").setText(Float.toString(graph.valueTop));
     
     
     
          List l = Arrays.asList("all logs", "data", "comments");
  cp5.addScrollableList("consolemode")
     .setPosition(25, 625)
     .setSize(200, 300)
     .setBarHeight(40)
     .setItemHeight(40)
     .setFont(font)
     .addItems(l);
     
     
     cp5.addButton("exporttocsv")
     .setValue(0)
.setPosition(25, 575)
     .setFont(font)
     .setSize(200, 50)
     ;
    
     
}

public class Graph{
  public Graph(float x, float y, float sizeX, float sizeY) {
    this.x=x;
    this.y=y;
    this.sizeX=sizeX;
    this.sizeY=sizeY;
  }
  
    public void setTextBoxesBasedOnGraph(){
  if (isFloat(cp5.get(Textfield.class,"xMin").getText())) if(Float.parseFloat(cp5.get(Textfield.class,"xMin").getText())!=(graph.valueLeftSide))cp5.get(Textfield.class,"xMin").setText(Float.toString(graph.valueLeftSide));
    if (isFloat(cp5.get(Textfield.class,"xMax").getText()))if(Float.parseFloat(cp5.get(Textfield.class,"xMax").getText())!=(graph.valueRightSide))cp5.get(Textfield.class,"xMax").setText(Float.toString(graph.valueRightSide));
    if (isFloat(cp5.get(Textfield.class,"yMin").getText()))if(Float.parseFloat(cp5.get(Textfield.class,"yMin").getText())!=(graph.valueBottom))cp5.get(Textfield.class,"yMin").setText(Float.toString(graph.valueBottom));
    if (isFloat(cp5.get(Textfield.class,"yMax").getText()))if(Float.parseFloat(cp5.get(Textfield.class,"yMax").getText())!=(graph.valueTop))cp5.get(Textfield.class,"yMax").setText(Float.toString(graph.valueTop));
  }
  
  
  public void getDataFromTextBoxes(){
  var xMin=cp5.get(Textfield.class,"xMin").getText();
   if (isFloat(xMin))graph.valueLeftSide=Float.parseFloat(xMin);
   
     var xMax=cp5.get(Textfield.class,"xMax").getText();
    if (isFloat(xMax))graph.valueRightSide=Float.parseFloat(xMax);
    
    var yMin=cp5.get(Textfield.class,"yMin").getText();
    if (isFloat(yMin))graph.valueBottom=Float.parseFloat(yMin);
    
     var yMax=cp5.get(Textfield.class,"yMax").getText();
    if (isFloat(yMax))graph.valueTop=Float.parseFloat(yMax);
  }
  
  float mousePositionOnGraphX=0;
    float mousePositionOnGraphY=0;
  void handelMouse(float x, float y,boolean isPressed){
    float newMousePositionOnGraphX=screenPositionToGraphPositionX(x);
    float newMousePositionOnGraphY=screenPositionToGraphPositionY(y);
    float mousePositionOnGraphXDelta=mousePositionOnGraphX-newMousePositionOnGraphX;
    float mousePositionOnGraphYDelta=mousePositionOnGraphY-newMousePositionOnGraphY;
    if(isPressed && isPointInsideGraph(newMousePositionOnGraphX,newMousePositionOnGraphY)){
      valueLeftSide+= mousePositionOnGraphXDelta;
      valueRightSide+= mousePositionOnGraphXDelta;
      valueTop+= mousePositionOnGraphYDelta;
      valueBottom+= mousePositionOnGraphYDelta;
    }
    float mousePositionOnGraphXAfterShift=screenPositionToGraphPositionX(x);
    float mousePositionOnGraphYAfterShift=screenPositionToGraphPositionY(y);
    
    mousePositionOnGraphX=mousePositionOnGraphXAfterShift;
    mousePositionOnGraphY=mousePositionOnGraphYAfterShift;
 
  }
  public void assignScale(String xMin, String xMax, String yMin, String yMax){
    if (isFloat(xMin))graph.valueLeftSide=Float.parseFloat(xMin);
    if (isFloat(xMax))graph.valueRightSide=Float.parseFloat(xMax);
    if (isFloat(yMin))graph.valueBottom=Float.parseFloat(yMin);
    if (isFloat(yMax))graph.valueTop=Float.parseFloat(yMax);
  }
    
  float x;
  float y;
  float sizeX;
  float sizeY;
  float dataPointSize;
  color graphColor=color(20,20,20);

  float valueLeftSide=0;
  float valueRightSide=100;

  float valueTop=100;
  float valueBottom=0;
  color dataPointColorT =color(250, 0, 0);
  color dataPointColorP =color(0, 250, 0);
  color dataPointColorH =color(0, 0, 250);
  color gridColor =color(250, 250, 250);
 color faintGridColor =color(100, 100, 100);
  float circleSize=10;

  public void renderPoint(float pX,float pY, color colour)
  {
    if(!isPointInsideGraph(pX, pY))return;
      
    fill(colour);
    float xRenderPosition=x;
    float yRenderPosition=y;
    float xRenderPositionTwo=x+sizeX;
    float yRenderPositionTwo=y+sizeY;

      float pointScaledX = (pX-valueLeftSide)/(valueRightSide-valueLeftSide);
      float pointScreenPosX = xRenderPosition+(pointScaledX*(sizeX));

      float pointScaledY = (pY-valueBottom)/(valueTop-valueBottom);
      float pointScreenPosY = yRenderPositionTwo+(pointScaledY*(-sizeY));

      circle(pointScreenPosX, pointScreenPosY, circleSize); 
      
      float distanceFromCursorX=pX-mousePositionOnGraphX;
      float distanceFromCursorY=pY-mousePositionOnGraphY;
      
      if((distanceFromCursorX*distanceFromCursorX)+(distanceFromCursorY*distanceFromCursorY)<1){//change from distance in graph units to distance in pixels
        text("("+pX+", "+pY+")",pointScreenPosX, pointScreenPosY);
      }
  }
  
  public void render() {
    fill(graphColor);
    rect(x, y, sizeX, sizeY);
    if(valueRightSide-valueLeftSide<=40){
    for (double i = valueLeftSide-(valueLeftSide%1)+(valueLeftSide>0?1:0); i <= valueRightSide; i+=1) {
      renderGridLine("", (float)i, valueBottom,(float)i,valueTop,faintGridColor);
    }
    }

    if(valueTop-valueBottom<=40){
      for (double i = valueBottom-(valueBottom%1)+(valueBottom>0?1:0); i <= valueTop; i+=1) {
      renderGridLine("", valueLeftSide,(float)i, valueRightSide,(float)i,faintGridColor);
      }
    
    }
    
    for (double i = valueLeftSide-(valueLeftSide%10)+(valueLeftSide>0?10:0); i <= valueRightSide; i+=10) {
      renderGridLine(String.valueOf(i), (float)i, valueBottom,(float)i,valueTop,gridColor);
    }

      for (double i = valueBottom-(valueBottom%10)+(valueBottom>0?10:0); i <= valueTop; i+=10) {
      renderGridLine(String.valueOf(i), valueLeftSide,(float)i, valueRightSide,(float)i,gridColor);//valueLeftSide, (float)i,valueRightSide, (float)i);
      }
     

     
     
     
     
     
      for(dataPoint point : data) {

        renderPoint(point.time,point.p,dataPointColorP);
        renderPoint(point.time,point.h,dataPointColorH);
        renderPoint(point.time,point.t,dataPointColorT);   
  }
}

  public void AutoScaleX(){
    if(data.size()==0)return;
    float maxX=data.get(0).time;
    for(dataPoint point : data) {
      if(point.time>maxX)maxX=point.time;
    }
    
  valueRightSide=maxX+5;
  valueLeftSide=maxX-60*1;//should be able to change. currently you can see fie minutes.
  }
  
  public void AutoScaleY(){
   if(data.size()==0)return;
    float maxY=data.get(0).t;
    float minY=data.get(0).t;
 
    for(dataPoint point : data) {
      if(!isPointInsideGraphX(point.time))continue;
      if(point.t>maxY)maxY=point.t;
      if(point.h>maxY)maxY=point.h;
      if(point.p>maxY)maxY=point.p;
      if(point.t<minY)minY=point.t;
      if(point.h<minY)minY=point.h;
      if(point.p<minY)minY=point.p;
    }
    if(maxY==minY)return;
    valueTop=maxY+5;    //+5 and -5 so that it doesent look like the graph is going off the page and to avoid floating point problems.
    valueBottom=minY-5;
  
  }
  
  boolean isPointInsideGraph(float x,float y){
    if(x<=valueLeftSide)return false;
    if(x>=valueRightSide)return false;
    if(y<=valueBottom)return false;
    if(y>=valueTop)return false;
    return true;
  }
    boolean isPointInsideGraphX(float x){
    if(x<=valueLeftSide)return false;
    if(x>=valueRightSide)return false;
    return true;
  }
  
    boolean isPointInsideGraphY(float y){
    if(y<=valueBottom)return false;
    if(y>=valueTop)return false;
    return true;
  }
  
  //should return a vector
  float graphPositionToScreenPositionX(float pX){
   float pointScaledX = (pX-valueLeftSide)/(valueRightSide-valueLeftSide);
   float pointScreenPosX = this.x+(pointScaledX*(sizeX));
   return pointScreenPosX;
  }
  float graphPositionToScreenPositionY(float pY){
      float pointScaledY = (pY-valueBottom)/(valueTop-valueBottom);
      float pointScreenPosY = this.y+(pointScaledY*(-sizeY));
      return pointScreenPosY;
  }
  
  float screenPositionToGraphPositionY(float pY){
    float PointScaledY = (pY-this.y)/-sizeY;
    float graphPositionY = (PointScaledY*(valueTop-valueBottom))+valueTop;
    return graphPositionY;
  }
  
  
  
    float screenPositionToGraphPositionX(float pX){
    float PointScaledX = (pX-this.x)/sizeX;
    float graphPositionX = (PointScaledX*(valueRightSide-valueLeftSide))+valueLeftSide;
    return graphPositionX;
  }


    public void renderGridLine(String value,float xV,float yV,float xV2,float yV2,color colour){
      //if(!isPointInsideGraph(xV, yV))return;
      //if(!isPointInsideGraph(xV2, yV2))return;   //disabled due to floating point discrepancy. They should already be inside the graph.

    float xRenderPosition=x;
    float yRenderPosition=y;
    float xRenderPositionTwo=x+sizeX;
    float yRenderPositionTwo=y+sizeY;
   
    //fill(250,10,10);
    fill(colour);
    stroke(colour);

      float pX=xV;
      float pointScaledX = (pX-valueLeftSide)/(valueRightSide-valueLeftSide);
      float pointScreenPosX = xRenderPosition+(pointScaledX*(sizeX));


      float pY =yV;
      float pointScaledY = (pY-valueBottom)/(valueTop-valueBottom);
      float pointScreenPosY = yRenderPositionTwo+(pointScaledY*(-sizeY));

      float pX2=xV2;
      float pointScaledX2 = (pX2-valueLeftSide)/(valueRightSide-valueLeftSide);
      float pointScreenPosX2 = xRenderPosition+(pointScaledX2*(sizeX));


      float pY2 =yV2;
      float pointScaledY2 = (pY2-valueBottom)/(valueTop-valueBottom);
      float pointScreenPosY2 = yRenderPositionTwo+(pointScaledY2*(-sizeY));
      line(pointScreenPosX, pointScreenPosY,pointScreenPosX2, pointScreenPosY2);
      text(value, pointScreenPosX, pointScreenPosY);
      
    }
    
    
}


private static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    boolean serialReloadPlanned=false;
void draw() { 
  

  background(70);
  /*graph.assignScale(
    cp5.get(Textfield.class,"xMin").getText(),
    cp5.get(Textfield.class,"xMax").getText(),
    cp5.get(Textfield.class,"yMin").getText(),
    cp5.get(Textfield.class,"yMax").getText()
  );*/
  graph.getDataFromTextBoxes();
  
      graph.handelMouse(mouseX,mouseY,mousePressed);
    if(autoScaleX)graph.AutoScaleX();
    if(autoScaleY)graph.AutoScaleY();
    
    graph.setTextBoxesBasedOnGraph();
  
    if((myPort!=null)){
      String output =   (myPort.readStringUntil(10));//no idea why this works//myPort.readString();
      if(output!=null){
        
    if(output.startsWith("C,")){
    String consoleCurrent = String.valueOf(output);
    String[] values = output.split(",");
    if(values.length==7){
      data.add(
      new dataPoint(Float.parseFloat(values[3]),Float.parseFloat(values[4]),Float.parseFloat(values[5]),Float.parseFloat(values[6]))
      );
    }
      consoleLogData += output;
    }     
    else{
      consoleLogComments += output;
    }
    consoleLog += output;
      }
  }

  if(consoleMode==ConsoleMode.allLogs)consoleTextArea.setText(consoleLog);
  if(consoleMode==ConsoleMode.data)consoleTextArea.setText(consoleLogData);
  if(consoleMode==ConsoleMode.comments)consoleTextArea.setText(consoleLogComments);

  
  //fill(210,210,210);
    //background(250);
  //rect(0,0,800,40);
  
    graph.render();
    


}

void checkBox(float[] a) {
  autoScaleX=(a[0]==1);
  autoScaleY=(a[1]==1);
}


void consolemode(int n) {
consoleMode = ConsoleMode.values()[n];
  println(consoleMode);
}


public void Quantize(int theValue) {
  graph.valueLeftSide=graph.valueLeftSide-graph.valueLeftSide%1;//esentialy the same thing as Math.floor()
  graph.valueRightSide=graph.valueRightSide-graph.valueRightSide%1;
  graph.valueTop=graph.valueTop-graph.valueTop%1;
  graph.valueBottom=graph.valueBottom-graph.valueBottom%1;
  
  graph.setTextBoxesBasedOnGraph();
}

public void exporttocsv(){
  println("exported!");
  saveStrings("data.csv", new String[]{consoleLogData});//not sure why it has to be an array.

}

public class dataPoint {
  float p;
  float h;
  float t;
  float time;
  public dataPoint(float time, float p, float h, float t) {
  this.p=p;
  this.h=h;
  this.t=t;
  this.time=time;
  }
}
