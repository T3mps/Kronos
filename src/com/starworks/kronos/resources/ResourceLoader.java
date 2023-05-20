package com.starworks.kronos.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface ResourceLoader {

	public default CompletableFuture<Void> load() {
		var resources = Arrays.asList(getResourceList());
		var futures = new ArrayList<CompletableFuture<Void>>();
		File file;
		for (var resource : resources) {
			file = new File(ResourceManager.INSTANCE.fetchLocalResourcePath(resource));
			if (file.exists()) continue;
			futures.add(ResourceManager.INSTANCE.fetchWebResource(resource).thenApply(i -> null));
		}
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
	}

	public String[] getResourceList();
}
