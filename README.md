# Swofty Parkour
![badge](https://img.shields.io/github/v/release/Swofty-Developments/SwoftyParkour)
![badge](https://img.shields.io/github/last-commit/Swofty-Developments/SwoftyParkour)
[![badge](https://img.shields.io/discord/830345347867476000?label=discord)](https://discord.gg/atlasmc)
[![badge](https://img.shields.io/github/license/Swofty-Developments/SwoftyParkour)](https://github.com/Swofty-Developments/SwoftyParkour/blob/master/LICENSE.txt)

**[JavaDoc 1.0.0](https://swofty-developments.github.io/SwoftyParkour/)**

An advanced configurable 1.16.x Minecraft Plugin meant for making parkours.

[Image of the plugin](https://github.com/Swofty-Developments/SwoftyParkour/blob/master/image.png?raw=true)

## Table of contents

* [Getting started](#getting-started)
* [Listening to a ParkourEvent](#listening-to-plugin-events)
* [License](#license)

## Getting started

This API does not support stand-alone usage, and you will need to add the project jar into your **plugins** folder. An updated version of the API jar can be found inside of the releases tab on the right of this readme. This projects JavaDoc (documentation for every method) can be found [here](https://swofty-developments.github.io/SwoftyParkour/).

### Add SwoftyParkour to your project

![badge](https://img.shields.io/github/v/release/Swofty-Developments/SwoftyParkour)

First, you need to setup the dependency inside of your Java project. Replace **VERSION** with the version of the release.

> Maven
```xml
<dependency>
    <groupId>net.swofty</groupId>
    <artifactId>parkour</artifactId>
    <version>VERSION</version>
</dependency>
```

> Gradle
```gradle
dependencies {
    implementation 'net.swofty:parkour:VERSION'
}
```

## Listening to Plugin Events

To listen to a ParkourEvent, create a class that extends `ParkourEventHandler`. In this class you will be able to override three main different events;

```java
    public void onParkourStart(ParkourStartEvent e) {}
    public void onCheckpointHit(CheckpointHitEvent e) {}
    public void onParkourEnd(ParkourEndEvent e) {}
```

Example usage can be found as below;
```java
public class Example extends ParkourEventHandler {

    @Override
    public void onParkourStart(ParkourStartEvent e) {
        Player player = e.getPlayer();
        Location locationOfFirstCheckpoint = e.getParkour().getCheckpoints().get(0);
    }

    @Override
    public void onParkourEnd(ParkourEndEvent e) {
        Player player = e.getPlayer();
        Location locationOfFirstCheckpoint = e.getParkour().getCheckpoints().get(0);
        Long timeSpent = e.getTimeSpent();
    }

    @Override
    public void onCheckpointHit(CheckpointHitEvent e) {
        Player player = e.getPlayer();
        Location locationOfFirstCheckpoint = e.getParkour().getCheckpoints().get(0);
        Long timeSpent = e.getTimeSpent();
        Integer checkPointHit = e.getCheckpointHit();
        Location locationOfHitCheckpoint = e.getParkour().getCheckpoints().get(checkPointHit);
    }

}
```

To register your class as an eventhandler, merely call #getListenerRegistry on the SwoftyParkourAPI class. An example of use is shown below;

```java
    public static void register() {
        SwoftyParkourAPI.getListenerRegistry().registerEvent(Example.class);
    }
```

## License
SwoftyParkour is licensed under the permissive MIT license. Please see [`LICENSE.txt`](https://github.com/Swofty-Developments/SwoftyParkour/blob/master/LICENSE.txt) for more information.
