package org.keads.track;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.io.InputStream;

public class Track {
    @Id
    private ObjectId id;
    private String filename;
    private String format;

    private InputStream stream;

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
