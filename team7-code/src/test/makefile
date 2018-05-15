JC = javac
JFLAGS = -g -cp ../../lib/*:../main
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	BoardTest.java \
	DTileTest.java \
	LeastSymmetricPlayerTest.java \
	MostSymmetricPlayerTest.java \
	MPlayerTest.java \
	PTileTest.java \
	RandomPlayerTest.java \
	ServerTest.java \
	SPlayerTest.java

default: classes

classes: $(CLASSES:.java=.class)

test:
	java -jar ../../lib/junit-platform-console-standalone-1.2.0.jar --class-path .:./../main --scan-class-path

clean:
	$(RM) *.class