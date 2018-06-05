JC = javac
JFLAGS = -g -cp ../../lib/*:../main
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	APlayerTest.java \
	BoardSpaceTest.java \
	BoardTest.java \
	GameTest.java \
	IntegrationTest.java \
	LeastSymmetricPlayerTest.java \
	MostSymmetricPlayerTest.java \
	NetworkPlayerTest.java \
	RandomPlayerTest.java \
	TileTest.java

default: classes

classes: $(CLASSES:.java=.class)

test:
	java -jar ../../../lib/junit-platform-console-standalone-1.2.0.jar --class-path .:./../../../target/classes --scan-class-path