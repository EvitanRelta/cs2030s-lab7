# To give execution permission, run:
# chmod +x path/to/file.sh
clear
DIVIDER="\n$(seq -s= $(tput cols - 1) | tr -d '[:digit:]')\n"
header() {
  local header=$1
  printf "$DIVIDER$header\n"
}

# Compiling my java classes
#header "[Compiling 'cs2030s.fp']"
header "[Compiling 'cs2030s.fp']"
javac -Xlint:rawtypes,unchecked ./cs2030s/fp/*.java


# Compiling + running tests
header "[Compiling + Running 'Test1']"
javac -Xlint:rawtypes,unchecked Test1.java
java Test1 | grep -v ok | grep -P "(^\s+[^\.]|failed)"

header "[Compiling + Running 'Test2']"
javac -Xlint:rawtypes,unchecked Test2.java
java Test2 | grep -v ok | grep -P "(^\s+[^\.]|failed)"

header "[Compiling + Running 'Test3']"
javac -Xlint:rawtypes,unchecked Test3.java
java Test3 | grep -v ok | grep -P "(^\s+[^\.]|failed)"

header "[Compiling + Running 'Test4']"
javac -Xlint:rawtypes,unchecked Test4.java
java Test4 | grep -v ok | grep -P "(^\s+[^\.]|failed)"

header "[Compiling + Running 'Test5']"
javac -Xlint:rawtypes,unchecked Test5.java
java Test5 | grep -v ok | grep -P "(^\s+[^\.]|failed)"

header "[Compiling + Running 'Test6']"
javac -Xlint:rawtypes,unchecked Test6.java
java Test6 | grep -v ok | grep -P "(^\s+[^\.]|failed)"


# Check style
header "[Checking styles - 'cs2030s.fp.InfiniteList']"
java -jar ~cs2030s/bin/checkstyle.jar -c ~cs2030s/bin/cs2030_checks.xml cs2030s/fp/InfiniteList.java;


# Test generating 'javadoc'
header "[Generating javadoc - 'cs2030s.fp.InfiniteList']"
javadoc -quiet -private -d docs cs2030s/fp/InfiniteList.java
