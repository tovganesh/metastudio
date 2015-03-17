# MeTA Studio Command Line Switches #

A list of command line switches available with MeTA Studio are enlisted here with examples.

**Running MeTA Studio in daemon mode:**

```
   java -jar MeTA.jar --daemon
```

**Running an external script from command line in MeTA Studio:**

```
   java -jar MeTA.jar --script <script file name>
```

**Running an external script from command line in MeTA Studio (non-daemon mode):**

```
   java -jar MeTA.jar --ndscript <script file name>
```

> _The difference with `--script` option being that, no daemon services like the Federation service is started._

**Adding external libraries to MeTA Studio:**

```
   java -jar MeTA.jar --addlibs lib1 lib2 ...
```

> Note that lib1.jar, lib2.jar ... should be copied to ` lib/ext ` directory before running the above command.

**Running an application from MeTA Studio Archive file (.mar):**

```
   java -jar MeTA.jar [--mar | --ndmar] < .mar file>
```

> Note ` --mar ` will start MeTA Studio in daemon mode while ` --ndmar ` will not start the daemons (similar to ` --ndscript`). Refer to http://code.google.com/p/metastudio/wiki/MeTAStudioArchiveFormat for help on .mar format.