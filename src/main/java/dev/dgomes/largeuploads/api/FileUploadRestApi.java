package dev.dgomes.largeuploads.api;

import dev.dgomes.largeuploads.fileupload.StorageException;
import dev.dgomes.largeuploads.fileupload.StorageService;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/upload")
public class FileUploadRestApi {

    private final StorageService storageService;

    public FileUploadRestApi(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<String> uploadFile(
            final HttpServletRequest request) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (!isMultipart) {
            throw new StorageException("File not Multipart as expected!");
        }

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload();

        FileItemIterator iter;
        InputStream fileStream = null;
        String name = null;
        try {
            iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                name = item.getName();
                fileStream = item.openStream();

                if (!item.isFormField()) {
                    System.out.println("File field " + name + " with file name " + item.getName() + " detected.");
                    break;
                }
            }
        } catch (FileUploadException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>("upload error!", HttpStatus.BAD_REQUEST);
        }
        if (fileStream != null) {
            System.out.println("fileStream = " + fileStream.toString());
            storageService.store_stream(fileStream, name);
        }

        return new ResponseEntity<String>("uploaded successfully!", HttpStatus.OK);
    }
}
