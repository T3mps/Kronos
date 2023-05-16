package com.starworks.kronos.resources;

public final class InternalResourceLoader implements ResourceLoader {

	public String[] getResourceList() {
		return new String[] {
			"fonts/opensans/OpenSans-Bold.ttf",
			"fonts/opensans/OpenSans-Italic.ttf",
			"fonts/opensans/OpenSans-Light.ttf",
			"fonts/opensans/OpenSans-Medium.ttf",
			"fonts/opensans/OpenSans-Regular.ttf",
		};
	}
}
