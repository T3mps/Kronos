![logo](.githubassets/kronos_logo.svg)

[![Kronos Version](https://img.shields.io/badge/version-alpha%200.1-lightgrey)](https://acidfrog.net/)
[![Downloads](https://img.shields.io/github/downloads/tempsies/kronos/total)](https://github.com/Tempsies/Kronos)
[![License](https://img.shields.io/github/license/tempsies/kronos)](https://www.mozilla.org/en-US/MPL/2.0/)

[![Discord](https://img.shields.io/discord/880676053729837057?color=blue&label=discord&logo=Discord)](https://discord.gg/ChBNXJUvx2)

## Features
**Kronos is a open source, cross-platform game development library with a robust ecosystem.** Listed below are current features:
- Highly customaizable logging framework
- Math library ([JOML](https://github.com/JOML-CI/JOML))
- File system/tree
- Resource manager (local and web)
- JSON parsing ([GSON](https://github.com/google/gson))
- XML parsing ([VTD-XML](https://github.com/dryade/vtd-xml))
- Numerical parsing
- Entity Component System
- Lua Scripting
- Input polling & event dispatch
- Action system
- Robust jobs system
- Toolkit API included
- ImGui integration

## Downloading
Upon first download and run, a `kronos.xml` file will be generated in the working directory data folder. By default, this is `%appdata%/Kronos/data`. Inside, you will need to specify your license key acquired from ([kronosengine.com](https://kronosengine.com/)].

## Creating an application with Kronos
The only required implementations are a `Main` class, and a one which extends the `Application` base class.

```java
import com.starworks.kronos.core.EntryPoint;

public class Main {

	public static void main(String[] args) {
		new EntryPoint();
	}
}
```
```java
public class MyApplication extends Application {

	public MyApplication() {
	}

	@Override
	public void initialize() {
	}

	@Override
	public void update(TimeStep timestep) {
	}

	@Override
	public void fixedUpdate(TimeStep timestep) {
	}

	@Override
	public void render() {
	}
}
```
Go ahead and run the main class, which will result in a crash. Thereafter, two files, `configuration.xml` and `configuration.xsd`, will be generated in the classpath data folder. Inside of `configuration.xml` there is an attribute of the root element which specifies the target implementation of `Application`; left blank by default. Go ahead and fill in the FQN (fully-qualified name) of your `Application` implementation and run the project again.

[![Twitter](https://img.shields.io/twitter/follow/AcidFrogLLC?style=social)](https://twitter.com/AcidFrogLLC)
[![Forks](https://img.shields.io/github/forks/tempsies/kronos?style=social)](https://github.com/Tempsies/Kronos)
