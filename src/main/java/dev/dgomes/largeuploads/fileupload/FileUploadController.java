package dev.dgomes.largeuploads.fileupload;

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        List<FileInfo> fileInfos = storageService.loadAll().map(
                path -> {
                    String filename = path.getFileName().toString();
                    String url = MvcUriComponentsBuilder
                            .fromMethodName(FileUploadController.class,"serveFile",path
                                    .getFileName()
                                    .toString())
                            .build()
                            .toUri()
                            .toString();
                    return new FileInfo(filename, url);
                }
        ).collect(Collectors.toList());

        model.addAttribute("files", fileInfos);
        /*
        model.addAttribute("files", storageService.loadAll()
                .map(path -> MvcUriComponentsBuilder
                        .fromMethodName(FileUploadController.class,"serveFile",path
                                .getFileName()
                                .toString())
                        .build()
                        .toUri()
                        .toString())
                .collect(Collectors.toList()));
         */
        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(final HttpServletRequest request, RedirectAttributes redirectAttributes) {

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
            // retrieve the multi-part constituent items parsed from the request
            iter = upload.getItemIterator(request);

            // loop through each item
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                name = item.getName();
                fileStream = item.openStream();
                // check if the item is a file
                if (!item.isFormField()) {
                    System.out.println("File field " + name + " with file name " + item.getName() + " detected.");
                    break; // break here so that the input stream can be processed
                }
            }
        } catch (FileUploadException | IOException e) {
            // log / handle the error here as necessary
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", name + " upload error!");
            return "redirect:/";
        }

        if (fileStream != null) {
            System.out.println("fileStream = " + fileStream.toString());
            // a file has been sent in the http request
            // pass the fileStream to a method on the storageService so it can be persisted
            // note the storageService will need to be modified to receive and process the fileStream
            storageService.store_stream(fileStream, name);
        }
        redirectAttributes.addFlashAttribute("message", name + " uploaded successfully!");
        return "redirect:/";
    }

}