JC = javac
JFLAGS = -g
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Colors.java \
	ATile.java \
	DTile.java \
	PTile.java \
	Board.java \
	IPlayer.java \
	Server.java \
	MPlayer.java \
	LeastSymmetricPlayer.java \
	MostSymmetricPlayer.java \
	RandomPlayer.java \
	SPlayer.java \
	Turn.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class