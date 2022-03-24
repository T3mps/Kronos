package net.acidfrog.kronos.core.files;

public interface FileAdapter {

    public abstract Filek getFile(String path, FilekType type);

    public abstract Filek classpath(String path);

    public abstract Filek internal(String path);

    public abstract Filek external(String path);

    public abstract Filek absolute(String path);

    public abstract Filek local(String path);

    public abstract String getExternalDirectory();

    public abstract boolean isExternalDirectoryAvaliable();

    public abstract String getLocalDirectory();

    public abstract boolean isLocalDirectoryAvaliable();
    
}
