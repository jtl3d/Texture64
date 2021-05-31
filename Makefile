PROGRAM=Texture64
PROGRAM2=SSB64ImageFileAppender
PROGRAM3=SSB64FileAppender

# compile and create executable
all:
	javac -cp . $(PROGRAM).java
	jar cmf $(PROGRAM).mf $(PROGRAM).jar $(PROGRAM).class $(PROGRAM).java
	javac -cp . $(PROGRAM2).java
	jar cmf $(PROGRAM2).mf $(PROGRAM2).jar $(PROGRAM2).class $(PROGRAM2).java
	javac -cp . $(PROGRAM3).java
	jar cmf $(PROGRAM3).mf $(PROGRAM3).jar $(PROGRAM3).class $(PROGRAM3).java

# clean output
clean:
	rm -rf *.jar
	rm -rf *.class
	rm -rf *.rgba5551
	rm -rf *.rgba8888