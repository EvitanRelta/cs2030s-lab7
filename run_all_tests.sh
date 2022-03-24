# To give execution permission, run:
# chmod +x path/to/file.sh

DIVIDER="\n$(seq -s= $(tput cols - 1) | tr -d '[:digit:]')\n"
printf "$DIVIDER[Compiling 'cs2030s.fp']\n"; javac -Xlint:rawtypes,unchecked ./cs2030s/fp/*.java
printf "$DIVIDER[Compiling + Running 'Test1']\n"; javac -Xlint:rawtypes,unchecked Test1.java; java Test1
printf "$DIVIDER[Compiling + Running 'Test2']\n"; javac -Xlint:rawtypes,unchecked Test2.java; java Test2
printf "$DIVIDER[Compiling + Running 'Test3']\n"; javac -Xlint:rawtypes,unchecked Test3.java; java Test3
printf "$DIVIDER[Compiling + Running 'Test4']\n"; javac -Xlint:rawtypes,unchecked Test4.java; java Test4

printf "$DIVIDER[Compiling 'LazyList']\n"; javac -Xlint:rawtypes,unchecked LazyList.java
printf "$DIVIDER[Compiling + Running 'Test5']\n"; javac -Xlint:rawtypes,unchecked Test5.java; java Test5

# Check style
printf "$DIVIDER[Checking styles - 'cs2030s.fp']\n"; java -jar ~cs2030s/bin/checkstyle.jar -c ~cs2030s/bin/cs2030_checks.xml cs2030s/fp/*.java;
printf "$DIVIDER[Checking styles - root files]\n"; java -jar ~cs2030s/bin/checkstyle.jar -c ~cs2030s/bin/cs2030_checks.xml *.java;

# Test generating 'javadoc'
printf "$DIVIDER[Generating javadoc - 'cs2030s.fp.Lazy']\n"; javadoc -quiet -private -d docs cs2030s/fp/Lazy.java
printf "$DIVIDER[Generating javadoc - 'LazyList']\n"; javadoc -quiet -private -d docs LazyList.java
