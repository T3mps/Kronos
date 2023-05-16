package com.starworks.kronos.resources;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface ResourceLoader {

	public default List<CompletableFuture<InputStream>> load() {
		var resources = Arrays.asList(getResourceList());
		var result = new ArrayList<CompletableFuture<InputStream>>();
		File file;
		for (var resource : resources) {
			file = new File(ResourceManager.INSTANCE.fetchLocalResourcePath(resource));
			if (file.exists()) continue;
			result.add(ResourceManager.INSTANCE.fetchWebResource(resource));
		}
		return result;
	}

	public String[] getResourceList();
}
