<a href="https://bluzelle.com/">
    <img src='https://raw.githubusercontent.com/bluzelle/api/master/source/images/Bluzelle%20-%20Logo%20-%20Big%20-%20Colour.png' alt="Bluzelle" style="width: 100%"/>
</a>


# Tests

Download junit-platform-console-standalone-1.6.2.jar from Maven Central [repository](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone) or using terminal.

    $ wget https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.6.2/junit-platform-console-standalone-1.6.2.jar

Get the package from github.

    $ git clone https://github.com/aqoleg/blzjava.git

Create build directory.

    $ mkdir blzjava/build

Compile.

    $ find blzjava/test -name "*.java" > blzjava/test/tests.txt
    $ javac -d blzjava/build/ -cp junit-platform-console-standalone-1.6.2.jar:blzjava/src/ @blzjava/test/tests.txt

Run.

    $ java -jar junit-platform-console-standalone-1.6.2.jar -cp blzjava/build -p com.bluzelle

Wait several minutes until the tests are complete and read the result.