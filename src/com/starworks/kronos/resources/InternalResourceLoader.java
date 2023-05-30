package com.starworks.kronos.resources;

public final class InternalResourceLoader implements ResourceLoader {

	public String[] getResourceList() {
		return new String[] {
			"build/generate_project.bat",
			"build/generate_project.ps1",
			"build/kronos.bat",
			"build/kronos.py",
			"fonts/opensans/OpenSans-Bold.ttf",
			"fonts/opensans/OpenSans-Italic.ttf",
			"fonts/opensans/OpenSans-Light.ttf",
			"fonts/opensans/OpenSans-Medium.ttf",
			"fonts/opensans/OpenSans-Regular.ttf",
		};
	}
}
