package org.freeyourmetadata.ner.services;

public interface NERService {
    public String getName();
    public String[] getPropertyNames();
    public String getProperty(String name);
    public void setProperty(String name, String value);
}
