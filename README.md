# largeuploads
Spring Boot very large uploads

## To run 
`docker-compose up`
The initial build takes some minutes depending on the Internet connection.
Wait until the terminal stops scrolling.

## To test
Open a web-browser on `http://127.0.0.1`

There’s a simple form to upload a file.
Any uploaded files will be listed on the web-page, under the form.
The files are saved on the folder `upload_folder` in the root directory. This folder is mapped into the spring_run container as a volume on docker-compose.
For testing a large file upload, my suggestion is to delete the actual `upload_folder` and use a separate partition or external drive as the `upload_folder` by using a symbolic link.
For example:
`ln -s /dev/sdb/some_folder ./upload_folder`
Then, on terminal, you can run a `watch df -h` during the large file upload to see that used disk space remains fixed while the destination partition will increase. 
