_(This page is currently under construction. Author: V. Ganesh)_


# Pre-requisites #

You should install MeTA Studio on each of the nodes that you wish to use for computation using MapReduce implementation in MeTA Studio. For details on installing MeTA Studio on your system please see the [Installation Guide](http://code.google.com/p/metastudio/wiki/Installation).

After installation, ensure that you have the latest version running by using the online update feature. The project page http://code.google.com/p/metastudio/ lists the latest version available. If this is not the version you see when you start MeTA Studio you need to apply the online update first. Also see the blog http://tovganesh.blogspot.com/search/label/MeTA for any issues pertaining to online updates, improvements and bug fixes for resolving peculiar issues, in particular check out [this issue here](http://tovganesh.blogspot.com/2009/02/meta-studio-update-map-reduce-editor.html).

# Setting up MeTA Studio environment #

MeTA Studio can be run in normal GUI mode (which is default) or can be run in daemon mode.
To start MeTA Studio in default GUI mode use the following command:
```
   $ cd <to the directory where MeTA Studio is installed>/bin
   $ java -jar MeTA.jar
```

To start MeTA Studio in non-GUI mode (daemon) use the following command:

```
   $ cd <to the directory where MeTA Studio is installed>/bin
   $ java -jar MeTA.jar --daemon
```

For more ways to start MeTA Studio see http://code.google.com/p/metastudio/wiki/MeTAStudioCommanLineSwitches.

Once you are sure that you can start the IDE or the daemon mode, you may require to set up your system to enable the MeTA Studio instances talk to each other.

Simplest way to check this is by issuing:
```
    sendMessage("<an IP in your network where MeTA Stdio is running>", "hello");
```

from the BeanShell (accessible from Tools -> Open BeanShell).

The default setup of MeTA Studio should popup a dialog on the other end requesting for user intervention to accept or reject a `BROADCAST` request. If you click yes the above message will appear on the remote machine in the runtime log on the remote machine.

If MeTA Studio is started in daemon mode, the request will be denied by default and an error will be shown in the runtime log of the IDE instance from where `sendMessage(...);` was invoked.

Some basic information on tuning federation security is provided at http://code.google.com/p/metastudio/wiki/MapReduceInMeTAStudio.

Once the above is set up, you can open up the code editor (Tools -> Open Code Editor).
And load `mapreducetest.bsh`, available from one of the following sources:

  1. using low level APIs http://tovganesh.googlepages.com/mapreduce.zip  or
  1. using wrapper functions http://tovganesh.googlepages.com/mapreduce_new.zip

Then click on the `Run` button to initiate the MapReduce testing. If every thing works out fine you should get the final results.