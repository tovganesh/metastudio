_(This page is currently under construction. Author: V. Ganesh)_

# Introduction #

MapReduce is an elegant, distributed software framework originally introduced by Google `[1, 2]` for processing large data sets on a distributed cluster.

A MapReduce application basically consists of the following components:
  * **A "Map" function**, the function describing actual computation on one or more data items to produce one or more set of results. This function can run on one more nodes as available. In MeTA Studio this function will run on all "discoverable" nodes from the underlying Federation framework.
  * **A "Reduce" function**, the function describing aggregation of results obtained from the "Map" operation so as to produce the output in a final form. This is usually run on the originating node.
  * **An underlying distributed framework**, that appropriately does distribution of the "Map" function and handles faults when they occur. It is also responsible for final data gathering via the "Reduce" function.

# Details #

To get an idea of using MapReduce in MeTA Studio download an example script available here:

  1. using low level APIs http://tovganesh.googlepages.com/mapreduce.zip  or
  1. using wrapper functions http://tovganesh.googlepages.com/mapreduce_new.zip

## How to write MapReduce scripts in MeTA Studio ##

The above package consists of three scripts:
  * **map.bsh** This corresponds to the "Map" function indicated above. The function starts off by first obtaining a list of data items that this instance of Map function has been assigned to process. Note that since there is only one Map function, only one type of calculation can be performed in one "MapReduce" run on all the data items. The line:

```
   data = getData(); 
```

The above call returns an `ArrayList<Serializable>` data list. This means that all the data items that you need to process should be `Serializable`, in lay terms meaning it should be able to be passed over network. Hence they cannot be objects like `File`, which are local to a machine. But standard data type like `int, double, String` etc. are automatically Seriablizable. So, for most practical purposes, you may not even have to worry about this. For a more information on specific needs to Serialize an object in custom way you can see Ref. `[3]`.

Next, each item in the data list can be obtained by calling:

```
   itm = data.get(i);  // 'i' can take values from 0 to data.size()-1
```

Now the "Map" function should do what ever processing it intends to do with the data items at this stage. In this case, the data items are just integers, and the processing performed is just adding them up. The result is the sum of all the data items.

Once the result is available, it should be packaged back as an `ArrayList<Serializable>` data list. This is because the result can either be a single quantity or multiple quantities, depending upon your application. In this particular case, it turns out to be a single number. The following lines basically achieve this:

```
   pdata = arraylist();
   pdata.add(res);

   setData(pdata);
```

Note the call to `setData();` is a way to pass back information (result) back to the underlying MapReduce framework which it later needs to utilise when calling the "Reduce" function.

  * **reduce.bsh** This corresponds to the "Reduce" function as indicated in Introduction. The anatomy of "Reduce" function is almost akin to "Map" function, with some major differences. The "Reduce" function is run only on the originating node, and is usually run sequentially. Further, a "Reduce" function merely assembles the results and rarely does any computations, if any. In the example, the "Reduce" function merely sums up partial sums provided as a result of execution of "Map" function on multiple nodes.

  * **mapreducetest.bsh** This script vaguely corresponds to the underlying distributed framework indicated in the Introduction. However, this is just a top level abstraction and shows how a MapReduce application is started from the originating node. The first few lines in the script import the packages that are required for using MapReduce framework. Next few comments indicate how to manually include nodes, in case you are not on a Local LAN. Note that each of the node which you wish to include should have instances of MeTA Studio running with properly configured Federation security rules (explained in next section). If every thing is OK, the following line initialises the framework:

```
   mapreduce = new MapReduceControllerFederationImpl();
```

Next, the data (input) to be processed is prepared and stored in list of type `ArrayList<Serializable>`. Examples of this data may include: list of Molecule objects, numbers, grid-points or what ever that is needed to be computed by your application.

Once this is done, the MapReduce operation is actually executed using the following:

```
   mapreduce.executeTask(new MapFunctionScriptHelper("/home/ganesh/meta/scripts/map.bsh"),
                   new ReduceFunctionScriptHelper("/home/ganesh/meta/scripts/reduce.bsh"),
                   data);
```

The first two parameters are paths to **map.bsh** and **reduce.bsh** scripts on the originating machine, and the last argument is the data to be processed.

Final result can be obtained by calling the following:

```
   res = mapreduce.getTaskData();
```

All the above can be bypassed if you use the wrapper function `mapreduce()` in the following manner:

```
   res = mapreduce("/home/ganesh/meta/scripts/map.bsh",
                   "/home/ganesh/meta/scripts/reduce.bsh", 
                   data);
   print(res);
```

## Federation APIs and MapReduce in MeTA Studio ##

The MapReduce functionality in MeTA Studio is implemented over the Federation `[4]` APIs. The Federation APIs in MeTA Studio provide a way to automatically discover instances of MeTA Studio running over a network and allow to build myriad of applications `[5]` in a way that hides the actual network APIs being used. Current implementation uses SSL socket as a means of communicating among nodes. However, low level functions like initiating a connection etc. are completely hidden by the Federation framework allowing a neat and easy way to achieve peer-to-peer networking or collaborative computing.

As there are potential security issue any such framework, a very simple "security shield" framework is provided which allows users to specify the nodes, type of service and the action to be be taken in case a node requests for a service. These settings can be changed from UI as indicated http://tovganesh.blogspot.com/2008/09/meta-studio-update-new-features-and-ui.html. Or if you are running MeTA Studio in [daemon mode](http://code.google.com/p/metastudio/wiki/MeTAStudioCommanLineSwitches), `~/.meta2.0/federationSecurity.xml` file could be modified to allow or disallow services offered by Federation framework.

For MapReduce to work properly, two services, namely `MAP_REDUCE` and `ECHO` should be allowed (set action to `ALLOW`) from each of the node participating in the MapReduce function.

## Setting up MeTA Studio environment for MapReduce ##

For details on setting up MeTA Studio environment for MapReduce refer to http://code.google.com/p/metastudio/wiki/SettingupMeTAStudioForMapReduce

# References #
`[1]` For the original MapReduce paper, refer to the Google labs page http://labs.google.com/papers/mapreduce.html.

`[2]` Wikipedia entry on MapReduce http://en.wikipedia.org/wiki/MapReduce, and the references there in.

`[3]` Serializable interface in Java http://java.sun.com/j2se/1.5.0/docs/api/java/io/Serializable.html.

`[4]` Refer to API documentation of MeTA Studio in Help section.

`[5]` "MeTA studio: A cross platform, programmable IDE for computational chemist", V. Ganesh, _J. Comput. Chem._, **30**, 661 (2009).