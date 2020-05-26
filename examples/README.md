<a href="https://bluzelle.com/">
    <img src='https://raw.githubusercontent.com/bluzelle/api/master/source/images/Bluzelle%20-%20Logo%20-%20Big%20-%20Colour.png' alt="Bluzelle" style="width: 100%"/>
</a>


## Server.jar

Download server.jar from [releases](https://github.com/aqoleg/blzjava/releases) or using terminal.

    $ wget https://github.com/aqoleg/blzjava/releases/download/0.4.0/server.jar

Run.

    $ java -jar server.jar 5000

Open other terminal.
Create connection.

    $ curl --data '{method:connect,args:["around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car","http://testnet.public.bluzelle.com:1317"]}' localhost:5000

Use any method described in [API docs](../src/com/bluzelle).

    curl --data '{method:create,args:[key,value,{gas_price:90},{seconds:90}]}' localhost:5000
    curl --data '{method:read,args:[key]}' localhost:5000
    curl --data '{method:deleteAll}' localhost:5000
    curl --data '{method:has,args:[key]}' localhost:5000


## Compile from the source code

Get the package from github.

    $ git clone https://github.com/aqoleg/blzjava.git

Create build directory.

    $ mkdir blzjava/build

Compile.

    $ javac -sourcepath blzjava/src/ -d blzjava/build/ blzjava/examples/Crud.java

Move to the build directory.

    $ cd blzjava/build/

Run.

    $ java Crud -c 'newKey' 'some value'
    $ java Crud -u 'newKey' 'updated value'
    $ java Crud -r 'newKey'
    $ java Crud -d 'newKey'


## Use library

Download bluzelle.jar from [releases](https://github.com/aqoleg/blzjava/releases) or using terminal.

    $ wget https://github.com/aqoleg/blzjava/releases/download/0.4.0/bluzelle.jar

Create file Threads.java.

    $ cat > Threads.java

Copy-paste the code from file "Threads.java" in the terminal, press enter, then ctrl+z.

Compile.

    $ javac -cp ./bluzelle.jar Threads.java

Run.

    $ java -cp .:./bluzelle.jar Threads
