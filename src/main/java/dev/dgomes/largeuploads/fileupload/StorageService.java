package dev.dgomes.largeuploads.fileupload;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void store_stream(InputStream fileStream, String name);
}