![logo](.githubassets/kronos_logo.svg)

[![Kronos Version](https://img.shields.io/badge/version-alpha%200.1-lightgrey)](https://acidfrog.net/)
[![Downloads](https://img.shields.io/github/downloads/tempsies/kronos/total)](https://github.com/Tempsies/Kronos)
[![License](https://img.shields.io/github/license/tempsies/kronos)](https://www.mozilla.org/en-US/MPL/2.0/)

[![Discord](https://img.shields.io/discord/880676053729837057?color=blue&label=discord&logo=Discord)](https://discord.gg/ChBNXJUvx2)

## Features
**Kronos is a open source, cross-platform game development library with a robust ecosystem.** Listed below are current and planned (\*) features:
- Highly customaizable, concurrent logging framework
- Math library ([JOML](https://github.com/JOML-CI/JOML))
- File system/tree
- JSON parsing ([GSON](https://github.com/google/gson))
- XML parsing ([VTD-XML](https://github.com/dryade/vtd-xml))
- Numerical parsing
- Archetypal ECS
- Application layer
- Window, key and mouse event handling
- Robust jobs system
- Toolkit API included
- Abstract rendering layer*
- ImGui integration* [WIP]
- Physics engine* [WIP]

## Creating an application with Kronos
The only required implementations are a class which holds the main method (Entry Point), and a class which extends the `Application` base class.

```
import com.starworks.kronos.core.EntryPoint;

public class Main {

	public static void main(String[] args) {
		new EntryPoint();
	}
}
```
```
public class MyApplication extends Application {

	public MyApplication() {
		super();
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

[![Twitter](https://img.shields.io/twitter/follow/AcidFrogLLC?style=social)](https://twitter.com/AcidFrogLLC)
[![Forks](https://img.shields.io/github/forks/tempsies/kronos?style=social)](https://github.com/Tempsies/Kronos)
