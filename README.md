# JSPlugins
Name subject to change

## Purpose
JSPlugins allows you to write simple, easy bukkit plugins using only JavaScript.  
You can dynamically change code and reload, without once compiling.  
This project aims at providing the same environment as traditional, Java-based bukkit scripting – and we think we're getting there

## Installation
To install this project, download the newest build and put it into the ./plugins directory of your bukkit installation. 
Then, place your scripts inside the ./jsplugins directory. Either create the directory manually or run the server first.  
That's all! You're good to go.  
**A java version of at least 1.8 is required to run or build this project**

## Manual Build
If you do not trust the binaries provided, you're invited to build this project yourself!  
### Dependencies
The only dependency of this project is the bukkit.jar provided by any distributor.
### Project language level
This project requires a language level of at least 1.8 due to the use of lambdas and, most importantly, the Nashorn engine.

## Sample scripts
A collection of sample JavaScript plugins is provided in the ./sample directory of the repository.  
An empty plugin requires at least the following structure:
```js
function onEnable() {

}

function onDisable() {

}

function getName() {
    return "EmptyPlugin";
}

function getVersion() {
    return "0.1";
}
```
Listening to events is as easy as registering them via 
```js
$.on
```
inside the *onEnable* function. A sample is provided below.  
*Note: The event type is the name of the event class as specified in the bukkit specification.*
```js
function onEnable() {
    $.on("BlockPlaceEvent", function(event) {
        event.getPlayer().sendMessage("Please don't place any blocks here.");
    });
}
```

### Modules
We provide optional modules that contain convenience classes and methods.  
In order to include a module, place it inside the jsplugins/modules directory and include it as such:
```js
load("jsplugins/modules/<name>")
```
An example using the HTTP module is provided in the samples directory.

## Commands
The following commands are available:
- *js help*, shows a list of available actions
- *js list*, lists all loaded JavaScript plugins
- *js load <plugins...>*, loads one (or more) plugins by filename. File must be in the ./jsplugins directory of the server.
- *js unload <plugins...>*, unloads one (or more) plugins by filename.
- *js reload [plugins...]*, reloads the specified or, if none specified, all plugins