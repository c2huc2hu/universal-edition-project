del *.jar
del *.class
javac -cp "c:\Program Files\leJOS-EV3\lib\ev3\ev3classes.jar" -target 1.7 *.java
jar -cfe PizzaGrab.jar Main * 
