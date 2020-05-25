<a href="https://bluzelle.com/">
    <img src='https://raw.githubusercontent.com/bluzelle/api/master/source/images/Bluzelle%20-%20Logo%20-%20Big%20-%20Colour.png' alt="Bluzelle" style="width: 100%"/>
</a>


## Compile from the source code

Get the package from github.

    $ git clone https://github.com/aqoleg/blzjava

Create build directory.

    $ mkdir blzjava/build

Compile.

    $ javac -sourcepath blzjava/src/ -d blzjava/build/ blzjava/examples/Crud.java

Move to the build directory.

    $ cd blzjava/build/

Run

    $ java Crud -c newKey 'some value'
    $ java Crud -u newKey 'updated value'
    $ java Crud -r newKey
    $ java Crud -d newKey