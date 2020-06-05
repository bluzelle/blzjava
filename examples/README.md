<a href="https://bluzelle.com/">
    <img src='https://raw.githubusercontent.com/bluzelle/api/master/source/images/Bluzelle%20-%20Logo%20-%20Big%20-%20Colour.png' alt="Bluzelle" style="width: 100%"/>
</a>


## Server.jar

Build from the source code.
Get the package from github.

    $ git clone https://github.com/aqoleg/blzjava.git

Create output directory.

    $ mkdir blzjava/out

Compile.

    $ javac -cp blzjava/src/:blzjava/examples/ -d blzjava/out/ blzjava/examples/server/Server.java

Create file server.jar.

    $ cd blzjava/out/
    $ jar cfe ../../server.jar server.Server ./
    $ cd ../../

The same file can be downloaded from [releases](https://github.com/aqoleg/blzjava/releases) or using terminal.

    $ wget https://github.com/aqoleg/blzjava/releases/download/0.4.1/server.jar

Run.

    $ java -jar server.jar 5000

Open other terminal.
Create connection.

    $ curl --data '{method:connect,args:["around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car","http://testnet.public.bluzelle.com:1317"]}' localhost:5000

Use any method described in [API docs](../src/com/bluzelle).

    curl --data '{method:create,args:[key,value,{gas_price:10},{seconds:90}]}' localhost:5000
    curl --data '{method:read,args:[key]}' localhost:5000
    curl --data '{method:deleteAll}' localhost:5000
    curl --data '{method:has,args:[key]}' localhost:5000



## CRUD

Get the package from github.

    $ git clone https://github.com/aqoleg/blzjava.git

Create output directory.

    $ mkdir blzjava/out

Compile.

    $ javac -cp blzjava/src/ -d blzjava/out/ blzjava/examples/Crud.java

Move to the output directory.

    $ cd blzjava/out/

Run. Read help.

    $ java Crud

Create new key.

    $ java Crud -c 'newKey' 'some value'

Read value of the key.

    $ java Crud -r 'newKey'

Update existing key.

    $ java Crud -u 'newKey' 'updated value'

Delete existing key.

    $ java Crud -d 'newKey'



## One more example

Download bluzelle.jar from [releases](https://github.com/aqoleg/blzjava/releases) or using terminal.

    $ wget https://github.com/aqoleg/blzjava/releases/download/0.4.1/bluzelle.jar

Copy file Threads.java or get file from the package.

    $ git clone https://github.com/aqoleg/blzjava.git
    $ mv blzjava/examples/Threads.java Threads.java

Compile.

    $ javac -cp ./bluzelle.jar Threads.java

Run.

    $ java -cp .:./bluzelle.jar Threads

This will create multiple key-value pairs and simultaneously read all the keys every 2 seconds.
