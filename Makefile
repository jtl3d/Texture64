PROGRAM=Texture64

# compile and create executable
all:
	javac -cp . $(PROGRAM).java
	jar cmf $(PROGRAM).mf $(PROGRAM).jar $(PROGRAM).class $(PROGRAM).java

# clean output
clean:
	rm -rf *.jar
	rm -rf *.class
	rm -rf *.rgba5551
	rm -rf *.rgba8888